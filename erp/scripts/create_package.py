import sys
import os
import shutil
import io

# Script to create directory structure and create config file from template files

# python create_package.py [app_name] [family_name] [class_name] [app_version] [path_to_folder_with_jars] 

def main(args):
	app_name = args[1]
	family_name = args[2]
	class_name = args[3]
	version = args[4]
	path_to_folder_with_jars = args[5]

	create_directory_structure(app_name)
	bin_folder = os.path.join(os.path.getcwd(),app_name,"bin")
	copy_jars_to_bin(bin_folder,path_to_folder_with_jars)
	create_bash_script_file(bin_folder,app_name)	

	default_folder = os.path.join(os.path.getcwd(),app_name,"default")	
	if not os.path.exists(default_folder):
		print "Unable to find default directory"
		exit(0);
	
	create_config_files(default_folder,family_name,class_name,app_name,verion)

def create_directory_structure(app_name):
	
	if os.path.exists(app_name):
		shutil.rmtree(app_name)

	os.makedirs(app_name)
	os.chdir(os.path.join(os.getcwd(),app_name))

	if not os.path.exists("bin"):
		os.makedirs("bin")
		print "creating bin folder"

	os.chdir(os.path.join(os.getcwd(),".."))

	static_folder = os.path.join(app_name,"appserver","static")

	if not os.path.exists(static_folder):
		os.makedirs(static_folder)
		create_html_documentation(app_name, static_folder)	
		print "creating appserver folder"

	default_folder = app_name + "/default"	

	if not os.path.exists(default_folder):
		print "creating default folder"
		os.makedirs(default_folder)

		create_indexes_conf()
		data_folder = default_folder + "/data"
		
		if not os.path.exists(data_folder):
			print "creating data folder"
			os.makedirs(data_folder)

			ui_folder = data_folder + "/ui"

			if not os.path.exists(ui_folder):
				print "creating ui folder"
				os.makedirs(ui_folder)

				nav_folder = ui_folder + "/nav"

				if not os.path.exists(nav_folder):	
					print "creating nav folder"
					os.makedirs(nav_folder)
					create_default_file(nav_folder)

				views_folder = ui_folder + "/views"
					
				if not os.path.exists(views_folder):
					print "creating views folder"		
					os.makedirs(views_folder)
					create_documentation_file(app_name, views_folder)

def copy_jars_to_bin(path_to_folder_with_jars,bin_folder):
	try:
		shutil.copytree(path_to_folder_with_jars,bin_folder)
	except shutil.Error as e:
		print('Directory not copied. Error: %s' % e)
	except OSError as e:
		print('Directory not copied. Error: %s' % e)

def create_config_files(default_folder,family_name,class_name,app_name,version):
	os.chdir(default_folder)
	create_indexes_conf_file(family_name,class_name,app_name)
	create_apps_conf_file(family_name,class_name,app_name,version)

def create_bash_script_file(bin_folder,app_name):
	file_name = os.path.join(bin_folder,"erp_script.sh")
	template_file_name = os.path.abspath(os.getcwd() + "/../template_files/erp_script.sh")
	config = io.open(file_name,'w')

	for line in io.open(template_file_name, 'r'):
		line = line.replace('$app-name',app_name)
		config.write(line)

	config.close()

def create_default_file(nav_folder_path):
	file_name = os.path.join(nav_folder_path,"default.xml")
	template_file_name = os.path.abspath(os.getcwd() + "/../template_files/default.xml")
	config = io.open(file_name,'w')

	for line in io.open(template_file_name, 'r'):
		config.write(line)

	config.close()

def create_documentation_file(app_name, views_folder_path):
	file_name = os.path.join(views_folder_path, "Documentation.xml")
	template_file_name = os.path.abspath(os.getcwd() + "/../template_files/Documentation.xml")
	config = io.open(file_name,'w')

	for line in io.open(template_file_name, 'r'):
		line = line.replace('$app-name',app_name)
		config.write(line)

	config.close()
	
def create_indexes_conf_file(family_name,app_name,class_name):
	file_name = os.path.join(os.getcwd(),"indexes.conf")
	template_file_name = os.path.abspath(os.getcwd() + "/../template_files/indexes.conf")
	config = io.open(file_name,'w')

	for line in io.open(template_file_name, 'r'):
		line = line.replace('$app-name',app_name)
		line = line.replace('$family-name',family_name)
		line = line.replace('$class-name',class_name)
		config.write(line)

	config.close()

def create_app_conf_file(family_name,version):
	file_name = os.path.join(os.getcwd(),"app.conf")
	template_file_name = os.path.abspath(os.getcwd() + "/../template_files/app.conf")
	config = io.open(file_name,'w')

	for line in io.open(template_file_name, 'r'):
		line = line.replace('$family-name',family_name)
		line = line.replace('$version',version)
		config.write(line)

	config.close()

def create_html_documentation(app_name, static_folder_path):
	file_name = os.path.join(static_folder_path,app_name + ".html")

	data = "<html>\n<head>\n<title>" + app_name + "</title>\n</head>\n<body></body>\n</html>";
	fp = open(file_name,'w')
	fp.write(data)
	fp.close()

if __name__ == '__main__':	
	
	args = sys.argv

	if args.__len__() != 5:
		print "Please enter correct arguments"
		print "Syntax : main_class $[app_name] $[family_name] $[implementing_class_name] $[version]"
		exit
	else:
		main(args)