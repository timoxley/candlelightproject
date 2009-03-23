PROJECT = loc2ip
SOURCE = $(PROJECT).c
PROJECT_DIR = candlelight/
REMOTE_DIR = /data/local/

local:
$(PROJECT):
	gcc loc2ip.c -o $(PROJECT)

remote:
	arm-none-linux-gnueabi-gcc -static $(SOURCE) -o $(PROJECT)
	adb push $(PROJECT) $(REMOTE_DIR)$(PROJECT_DIR)$(PROJECT)
	
	# $(PROJECT) built and pushed to emulator. 
	
