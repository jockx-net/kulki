echo off
set POM=C:\Users\Mateusz\IdeaProjects\kulki\pom.xml
set DALVIK_TOOLS=C:\Apps\dalvik-sdk
set SOURCE=C:\Users\Mateusz\IdeaProjects\kulki\target
set TARGET=C:\Users\Mateusz\IdeaProjects\kulki\target

@echo MAVEN BUILD
call mvn -f %POM% clean install assembly:single || exit /b

@echo GRADLE BUILD
call gradle -p%DALVIK_TOOLS%\android-tools -PDEBUG -PANDROID_SDK=%ANDROID_HOME% -PDIR=%TARGET% -PNAME=Kulki -PPACKAGE=net.jockx -PJFX_SDK=%DALVIK_TOOLS% -PJFX_APP=%SOURCE% -PJFX_MAIN=net.jockx.MainApp createProject || exit /b

@echo ANT BUILD
call ant -f %TARGET%\Kulki\build.xml debug || exit /b

@echo INSTALL ON DEVICE
call adb install -r %TARGET%\Kulki\bin\Kulki-debug.apk || exit /b
pause
adb logcat


