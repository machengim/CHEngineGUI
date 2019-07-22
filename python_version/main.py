import sys
import os
import admin


site_path = os.getcwd()
app_path = os.path.abspath(sys.argv[0])     #get the application location to handle theme resources.
app_path = os.path.dirname(app_path)
if len(sys.argv) < 2:
    print('Usage: \"' + sys.argv[0] + ' <option>\"', end = ", ")
    print('or type \"' + sys.argv[0] + ' help\" to get option list.')
elif sys.argv[1] == 'help':
    print('option list:\n'
          '    \"newsite\": create a new site in current directory;\n'
          '    \"newpost\": create a new post draft in draft directory;\n'
          '    \"newpage\": create a new page draft in draft directory;\n'
          '    \"check\": check modifications of drafts and modify the site if so;\n'
          '    \"refresh\": generate the entire site from scratch;\n'
          '    \"changename\": change the name of the site:\n'
          '    \"changeurl\": change the url of the site:\n'
          '    \"changetheme\": change the theme of the site.\n')
elif sys.argv[1] == 'newsite':
    admin.new_site(site_path, app_path)
elif sys.argv[1] == 'newpost':
    admin.new_draft(site_path, 'post')
elif sys.argv[1] == 'newpage':
    admin.new_draft(site_path, 'page')
elif sys.argv[1] == 'check':
    admin.check_modification(site_path)
elif sys.argv[1] == 'refresh':
    admin.refresh_site(site_path)
elif sys.argv[1] == 'changename':
    admin.change_name_url(site_path, 'name')
elif sys.argv[1] == 'changeurl':
    admin.change_name_url(site_path, 'url')
elif sys.argv[1] == 'changetheme':
    admin.change_theme(site_path, app_path)
else:
    print('Unknown command. Please type \"' + sys.argv[0] + ' help\" to get option list.')
