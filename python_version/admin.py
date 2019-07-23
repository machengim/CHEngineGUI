import os
import sys
import utils
import markdown2


def change_name_url(site_path, type):
    site_info = utils.load_site(site_path)
    new_value = input('please enter a new ' + type + ' for your site: ')
    if len(new_value) == 0:
        return
    site_info[type] = new_value
    generate_whole_site(site_path, site_info)


def change_theme(site_path, app_path):
    site_info = utils.load_site(site_path)
    new_theme = get_theme(site_path, app_path)
    new_theme_detail = utils.get_theme_detail(site_path, new_theme)
    site_info['theme_name'] = new_theme
    site_info['theme'] = new_theme_detail
    generate_whole_site(site_path, site_info)


def check_modification(site_path):
    site_info = utils.load_site(site_path)
    site_post_list, site_page_list = get_site_list(site_info)
    draft_post_list, draft_page_list = read_drafts_to_dict(site_path)
    flag1 = compare_list(site_path, site_info, draft_post_list, site_post_list, 'posts')
    flag2 = compare_list(site_path, site_info, draft_page_list, site_page_list, 'pages')
    if flag1 == 1 or flag2 == 1:
        site_info['posts'].sort(key=post_sort_key, reverse=True)
        generate_index(site_path, site_info)
        generate_archive(site_path, site_info)
        generate_cats(site_path, site_info)
        utils.save_site(site_info, site_path)


def compare_list(site_path, site_info, draft_list, post_list, type):
    draft_list.sort(key=list_sort_key)
    post_list.sort(key=list_sort_key)
    flag = i = j = 0
    while i < len(draft_list) and j < len(post_list):
        if draft_list[i]['id'] < post_list[j]['id']:
            process_draft(site_path, site_info,  draft_list[i]['filename'])
            i += 1
            flag = 1
            continue
        elif draft_list[i]['id'] > post_list[j]['id']:
            utils.delete_file(os.path.join(site_path, post_list[j]['content']['url']))
            site_info[type].remove(post_list[j]['content'])
            j += 1
            flag = 1
            continue
        else:
            draft_mtime = os.path.getmtime(draft_list[i]['filename'])
            post_mtime = post_list[j]['content']['mtime']
            if draft_mtime > post_mtime:
                utils.delete_file(os.path.join(site_path, post_list[j]['content']['url']))
                site_info[type].remove(post_list[j]['content'])
                process_draft(site_path, site_info, draft_list[i]['filename'])
                flag = 1
            i += 1
            j += 1
    while i < len(draft_list):
        process_draft(site_path, site_info, draft_list[i]['filename'])
        i += 1
        flag = 1
    while j < len(post_list):
        utils.delete_file(os.path.join(site_path, post_list[j]['content']['url']))
        site_info[type].remove(post_list[j]['content'])
        j += 1
        flag = 1
    return flag


def generate_archive(site_path, site_info):
    content = utils.translate(site_info, os.path.join(site_path, 'themes', site_info['theme_name'], 'archive.tpl'))
    output_file = os.path.join(site_path, 'archive', 'index.html')
    utils.write_file(content, output_file)


def generate_cats(site_path, site_info):
    for cat in site_info['cats']:
        content = utils.translate(site_info, os.path.join(site_path, 'themes', site_info['theme_name'], 'archive.tpl'), {}, '', cat['name'])
        output_file = os.path.join(site_path, cat['url'])
        utils.write_file(content, output_file)


def generate_index(site_path, site_info):
    content = utils.translate(site_info, os.path.join(site_path, 'themes', site_info['theme_name'], 'index.tpl'))
    output_file = os.path.join(site_path, 'index.html')
    utils.write_file(content, output_file)


def generate_post(metas, content, site_path, site_info, type):
    template = type + '.tpl'
    content = utils.translate(site_info, os.path.join(site_path, 'themes', site_info['theme_name'], template), metas, content)
    output_file = os.path.join(site_path, metas['url'])
    utils.write_file(content, output_file)


def generate_whole_site(site_path, site_info):
    site_info['posts'].clear()
    site_info['pages'].clear()
    site_info['cats'].clear()
    site_info['cat_names'].clear()
    draft_path = os.path.join(site_path, 'draft')
    if not utils.check_dir_exists(draft_path):
        print('No draft directory!')
        return
    for file in os.listdir(draft_path):
        if file.count('.md') > 0:
            process_draft(site_path, site_info, os.path.join(draft_path, file))
    site_info['posts'].sort(key = post_sort_key, reverse=True)
    generate_index(site_path, site_info)
    generate_archive(site_path, site_info)
    generate_cats(site_path, site_info)
    utils.save_site(site_info, site_path)


def get_site_list(site_info):
    site_post_list = []
    site_page_list = []
    for post in site_info['posts']:
        site_post_list.append({'id': post['id'], 'content': post})
    for page in site_info['pages']:
        site_page_list.append({'id': page['id'], 'content': page})
    return site_post_list, site_page_list


def get_theme(site_path, app_path):
    theme_lists = [f for f in os.listdir(os.path.join(app_path, 'themes')) if f.count('.zip') > 0]
    theme_lists_display = [f[ : f.index('.zip')] for f in theme_lists]
    print('We have themes as follow, please enter the number: ')
    for i in range(0, len(theme_lists)):
        print('    ' + str(i + 1) + ' : ' + theme_lists_display[i])
    theme_index = int(input()) - 1
    while theme_index > len(theme_lists) or theme_index < 0:
        theme_index = input('not in range! please enter again: ')
    theme_name = theme_lists[theme_index]
    theme_file = os.path.join(app_path, 'themes', theme_name)
    theme_dest_path = os.path.join(site_path, 'themes')
    utils.unzip(theme_file, theme_dest_path)
    return theme_name[ : theme_name.index('.zip')]


def initialize_dirs(site_path):
    draft_path = os.path.join(site_path, 'draft')       #Do not clear draft directory!
    if not utils.check_dir_exists(draft_path):
        utils.make_dir(draft_path)
    elif len(os.listdir(draft_path)) > 0:
        clear_drafts = input('draft files exist! Do you want to clear them? (y/n) :')
        if clear_drafts == 'y':
            utils.clear_dir(draft_path)
    dir_names = ['posts', 'pages', 'archive']
    dir_paths = [os.path.join(site_path, dir_name) for dir_name in dir_names]
    for dir_path in dir_paths:
        if not utils.check_dir_exists(dir_path):
            utils.make_dir(dir_path)
        if len(os.listdir(dir_path)) > 0:
            utils.clear_dir(dir_path)


def list_sort_key(element):
    return element['id']


def post_sort_key(post):
    return post['date']


def process_draft(site_path, site_info, filename):  #full path filename
    text = utils.read_file(filename)
    metas, content = utils.split_md_content(text)
    content = markdown2.markdown(content)
    if len(metas['title']) == 0:
        metas['title'] = 'No Title'
    metas['mtime'] = utils.get_now()
    if metas['is_page'] == 'y':
        if int(metas['id']) > site_info['next_page_id']:
            site_info['next_page_id'] = int(metas['id']) + 1
        if len(metas['url']) == 0:
            metas['url'] = 'p' + metas['id'] + '.html'
        metas['url'] = os.path.join('pages', metas['url'])
        
        generate_post(metas, content, site_path, site_info, 'page')
        site_info['pages'].append(metas)
    else:
        if int(metas['id']) > site_info['next_post_id']:
            site_info['next_post_id'] = int(metas['id']) + 1
        if len(metas['date']) == 0:
            metas['date'] = utils.get_today()
        if len(metas['url']) == 0:
            metas['url'] = metas['id'] + '.html'
        if len(metas['author']) == 0:
            metas['author'] = 'Cheng'
        metas['url'] = os.path.join('posts', metas['url'])
        metas['brief'] = utils.get_brief(content, site_info['theme']['brief_size'])   #need to get theme limit
        site_info['posts'].append(metas)
        if len(metas['cat']) > 0 and metas['cat'] not in site_info['cat_names']:
            cat = {'name': metas['cat'], 'url': os.path.join('archive', metas['cat'] + '.html')}
            site_info['cats'].append(cat)
            site_info['cat_names'].append(metas['cat'])
        generate_post(metas, content, site_path, site_info, 'post')


def new_draft(site_path, option):
    site_info = utils.load_site(site_path)
    draft_path = os.path.join(site_path, 'draft')
    if not utils.check_dir_exists(draft_path):
        utils.make_dir(draft_path)
    if option == 'post':
        draft_file = os.path.join(draft_path, str(site_info['next_post_id']) + '.md')
        draft_text = '---\n!Important: DO NOT modify the id number!\n' \
                     'id: ' + str(site_info['next_post_id']) + '\ntitle: \nauthor: \ndate: ' \
                     + utils.get_today() + '\nurl: ' + str(site_info['next_post_id']) + '.html\n' \
                     + 'cat: \n---\n\n'         #cat is short for category
        site_info['next_post_id'] += 1
    else:
        draft_file = os.path.join(draft_path, 'p' + str(site_info['next_page_id']) + '.md')
        draft_text = '---\n!Important: DO NOT modify the id number and is_page option!\n' \
                     'id: ' + str(site_info['next_page_id']) + '\ntitle: \nurl: p' + str(site_info['next_page_id']) \
                     + '.html\nis_page: y\n---\n\n'
        site_info['next_page_id'] += 1
    utils.write_file(draft_text, draft_file)
    utils.save_site(site_info, site_path)


def new_site(site_path, app_path):
    if utils.check_file_exists('save.ser'):
        overwrite = input('site data already exists, do you want to overwrite? (y/n)')
        if overwrite != 'y':
            return
    site_info = {}
    site_name = input('name of the site:')
    site_url = input('url of the site:')
    site_info['name'] = site_name
    site_info['url'] = site_url
    site_info['next_page_id'] = 1
    site_info['next_post_id'] = 1
    site_info['theme_name'] = get_theme(site_path, app_path)
    site_info['theme'] = utils.get_theme_detail(site_path, site_info['theme_name'])
    site_info['posts'] = []
    site_info['pages'] = []
    site_info['cats'] = []
    site_info['cat_names'] = []
    initialize_dirs(site_path)
    generate_index(site_path, site_info)
    utils.save_site(site_info, site_path)
    print('Congrats!')


def read_drafts_to_dict(site_path):
    draft_post_list = []
    draft_page_list = []
    draft_path = os.path.join(site_path, 'draft')
    if not utils.check_dir_exists(draft_path):
        print('draft path not exists!')
        sys.exit(1)
    for draft_file in os.listdir(draft_path):
        if draft_file.count('.md') == 0:
            continue
        text = utils.read_file(os.path.join(draft_path, draft_file))
        metas, _ = utils.split_md_content(text)
        draft_info = {'id': metas['id'], 'filename': os.path.join(site_path, 'draft', draft_file)}
        if metas['is_page'] == 'y':
            draft_page_list.append(draft_info)
        else:
            draft_post_list.append(draft_info)
    return draft_post_list, draft_page_list


def refresh_site(site_path):
    site_info = utils.load_site(site_path)
    site_info['theme'] = utils.get_theme_detail(site_path, site_info['theme_name'])
    generate_whole_site(site_path, site_info)

