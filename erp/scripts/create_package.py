import sys
import os
import shutil

# Script to create directory structure and create config file with default settings

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
    
def create_default_file(nav_folder_path):
	file_name = os.path.join(nav_folder_path,"default.xml")
	fp=open(file_name,'w')
	data = "<nav search_view=\"search\" color=\"#65A637\">\n"\
		 	"<view name=\"Documentation\" default='true' />\n" \
		 	"<view name=\"search\" default='false' />\n" \
		 	"<view name=\"data_models\" />\n" \
		 	"<view name=\"reports\" />\n" \
		 	"<view name=\"alerts\" />\n" \
		 	"<view name=\"dashboards\" />\n" \
    	 	"</nav>\n"
    
	fp.write(data)
	fp.close()

def create_documentation_file(app_name, views_folder_path):
	file_name = os.path.join(views_folder_path, "Documentation.xml")
	data = "<dashboard>\n" \
  			"<row>\n" \
  			"<html>\n" \
  			"<iframe src=\"/static/app/" + app_name + "/" + app_name + ".html\" style=\"width:100%; height: 800px;\"/>\n" \
  		   	"</html>\n" \
  		   	"</row>\n"\
		   	"</dashboard>\n" \

	fp = open(file_name,'w')	   
	fp.write(data)
	fp.close()

def create_html_documentation(app_name, static_folder_path):
	file_name = os.path.join(static_folder_path,app_name + ".html")

	data = "<html>\n<head>\n<title>" + app_name + "</title>\n</head>\n<body></body>\n</html>";
	fp = open(file_name,'w')
	fp.write(data)
	fp.close()

# default_folder,family_name,class_name,app_name,verion
def create_config_files(default_folder,family_name,class_name,app_name,version):
	os.chdir(default_folder)
	create_indexes_conf_file(family_name,class_name,app_name)
	create_apps_conf_file(family_name,class_name,app_name,version)
	
def create_indexes_conf(family_name,app_name,class_name):
	file_name = os.path.join(os.getcwd(),"indexes.conf")
	data = "[provider-family:" + family_name +"]\n" \
			"vix.command = $SPLUNK_HOME/etc/apps/" + app_name + "/bin/erp_script.sh\n" \
			"vix.command.arg.1 = " + class_name + "\n" \
	fp =open(file_name,'w')		
	fp.write(data)
	fp.close()

def create_apps_conf(family_name,app_name,class_name,version):
	file_name = os.path.join(os.getcwd(),"apps.conf")
	data = "# Splunk App Configuration file #\n" \
			"\n" \
			"[install]\n" \
			"is_configured = 0" \
			"\n" \
			"[ui]\n" \
			"is_visible = 1\n" \
			"label = " + app_name + "\n" \
			"\n" \
			"[launcher]\n" \
			"author = \n" \
			"description = \n" \
			"version = " + version "\n" \

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