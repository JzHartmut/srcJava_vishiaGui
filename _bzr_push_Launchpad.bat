@echo off
call bzr_call.bat V:\Bzr\srcJava_Zbnf exit
echo NOTE: SSH-key should be started
pause
call bzr launchpad-login hartmut-schorrig
pause
call bzr push lp:~hartmut-schorrig/jvishia-gui/trunk
pause
pause

