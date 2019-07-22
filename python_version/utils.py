import os
import sys
import json
import datetime
import time
import zipfile
import pickle


def check_child_url(url):
    if url.count('http') > 0 or url.count('.com') > 0 or url.count('.net') > 0 or url.count('.im') > 0:
        return url
    else:
        return '../' + url


def check_site_url(url):
    if url.count('http') == 0:
        return 'http://' + url
    else:
        return url

def check_dir_exists(dirname):
    return os.path.isdir(dirname)


def check_file_exists(filename):
    return os.path.isfile(filename)


def clear_dir(path):
    for f in os.listdir(path):
        os.remove(os.path.join(path, f))


def delete_file(filename):
    if not os.path.isfile(filename):
        return
    os.remove(filename)


def get_brief(content, limit):
    brief = content[:limit]
    if brief.count('<!') > 0:       #find <!--more--> tag.
        brief = brief[: brief.index('<!')]
    elif brief.count('<') > 0 and brief.rfind('<') > brief.rfind('>'):
        brief = brief[: brief.rfind('<')]
    end_chars = ['.','!','?','。','！','？']       #english and chinese character
    if brief[-1] not in end_chars:
        brief += '...'
    return brief


def get_now():
    return int(time.time())


def get_theme_detail(site_path, theme_name):
    config_file = os.path.join(site_path, 'themes', theme_name, 'config.json')
    with open(config_file, 'r') as f:
        config = json.load(f)
    return config


def get_today():
    today = datetime.date.today()
    return str(today)


def load_site(path):
    data_file = os.path.join(path, 'save.ser')
    if not check_file_exists(data_file):
        print('data file not exists!')
        sys.exit(0)
    with open(data_file, 'rb') as f:
        data = pickle.load(f)
    return data


def make_dir(dirname):
    os.mkdir(dirname)


def read_file(file):
    with open(file, 'r') as f:
        text = f.read()
    return text


def save_site(dict, path):
    data_file = os.path.join(path, 'save.ser')
    with open(data_file, 'wb') as f:
        pickle.dump(dict, f)


def split_md_content(text):
    parts = text.split('---', 2)
    meta_lines = parts[1].split('\n')
    metas = {}
    metas['is_page'] = 'n'
    parameters = ['id', 'title', 'author', 'cat', 'date', 'url', 'is_page']
    for line in meta_lines:
        if line.count(':') == 0:
            continue
        tag = line.split(':', 1)
        if tag[0] in parameters and len(tag[1]) > 0:
            metas[tag[0]] = tag[1].strip()
    return metas, parts[2]


def translate(site_info, template_file, metas = {}, content = '', cat = ''):
    if template_file.count('index.tpl') > 0:         #other posts and pages should add ../ to their url
        flag_index = 1
    elif template_file.count('archive.tpl') > 0:
        flag_index = 2
    elif template_file.count('page.tpl') > 0:
        flag_index = 3
    else:
        flag_index = 4      #post.tpl
    template = read_file(template_file)
    while template.count('[$for') > 0:
        i = template.index('[$for')
        j = template.index('$]', i + 1)
        j = template.index('[$endfor$]', j)
        template = template.replace(template[i: j + 10], translate_loop(site_info, template[i: j + 10], flag_index, cat))
    template = template.replace('[$site_name$]', site_info['name'])
    template = template.replace('[$site_url$]', check_site_url(site_info['url']))
    if flag_index != 1:
        template = template.replace('[$theme_path$]',check_child_url(os.path.join('themes', site_info['theme_name'])))
    else:
        template = template.replace('[$theme_path$]', os.path.join('themes', site_info['theme_name']))
    if flag_index == 3:
        template = template.replace('[$post_title$]', metas['title'])
        template = template.replace('[$post_url$]', metas['url'])
        template = template.replace('[$post_content$]', content)
    if flag_index == 4:             #index and archive handles these in loop
        template = template.replace('[$post_title$]', metas['title'])
        template = template.replace('[$post_author$]', metas['author'])
        template = template.replace('[$post_date$]', metas['date'])
        template = template.replace('[$post_url$]', metas['url'])
        template = template.replace('[$post_brief$]', metas['brief'])
        template = template.replace('[$post_content$]', content)
        if metas['cat'] is None:
            template = template.replace('[$post_cat$]', '')
        else:
            template = template.replace('[$post_cat$]', metas['cat'])
    return template


def translate_loop(site_info, text, flag_index, cat):
    i = text.index(':')
    j = text.index('$]', i + 1)
    loop_name = text[i + 1: j]
    text = text.replace(text[ :j + 2], '')
    text = text.replace('[$endfor$]', '')
    if loop_name == 'posts' or loop_name == 'recents' or loop_name == 'archives':
        return translate_loop_posts(site_info, text, loop_name, flag_index, cat)
    if loop_name == 'cats':
        loop_list = site_info[loop_name]
    else:
        loop_list = site_info['theme'][loop_name]
    loop_time = len(loop_list)
    if loop_time <= 0:
        return " "
    result = ''
    for i in range(0, loop_time):
        temp = text.replace('[$key$]', loop_list[i]['name'])
        if flag_index != 1:
            temp = temp.replace('[$value$]', check_child_url(loop_list[i]['url']))
        else:
            temp = temp.replace('[$value$]', loop_list[i]['url'])
        result += temp
    return result


def translate_loop_posts(site_info, text, option, flag_index, cat):
    if option == 'posts' :
        limit = min(int(site_info['theme']['posts_on_index']), len(site_info['posts']))
    elif option == 'recents':
        limit = min(int(site_info['theme']['recent_posts_size']), len(site_info['posts']))
    else:
        limit = len(site_info['posts'])
    result = ''
    post_list = site_info['posts']
    for i in range(0, limit):
        if len(cat) > 0 and post_list[i]['cat'] != cat:
            continue
        temp = text.replace('[$post_title$]', post_list[i]['title'])
        if flag_index == 1:
            temp = temp.replace('[$post_url$]', post_list[i]['url'])
        else:
            temp = temp.replace('[$post_url$]', check_child_url(post_list[i]['url']))
        temp = temp.replace('[$post_author$]', post_list[i]['author'])
        temp = temp.replace('[$post_date$]', post_list[i]['date'])
        temp = temp.replace('[$post_cat$]', post_list[i]['cat'])
        temp = temp.replace('[$post_brief$]', post_list[i]['brief'])
        result += temp
    return result


def unzip(filename, dest):
    with zipfile.ZipFile(filename, 'r') as zip_ref:
        zip_ref.extractall(dest)


def write_file(content, file):
    with open(file, 'w') as f:
        f.write(content)

