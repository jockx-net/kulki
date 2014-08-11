echo off
set POM=C:\Users\Mateusz\IdeaProjects\kulki\pom.xml
set DALVIK_TOOLS=C:\Apps\dalvik-sdk
set SOURCE=C:\Users\Mateusz\IdeaProjects\kulki\target
set TARGET=C:\Users\Mateusz\IdeaProjects\kulki\target
set NAME=Kulki

@echo MAVEN BUILD
call mvn -f %POM% clean install assembly:single || exit /b

@echo GRADLE BUILD
call gradle -p%DALVIK_TOOLS%\android-tools -PDEBUG -PANDROID_SDK=%ANDROID_HOME% -PDIR=%TARGET% -PNAME=%NAME% -PPACKAGE=net.jockx -PJFX_SDK=%DALVIK_TOOLS% -PJFX_APP=%SOURCE% -PJFX_MAIN=net.jockx.MainApp createProject || exit /b

@echo ANT BUILD
call ant -f %TARGET%\%NAME%\build.xml debug || exit /b

@echo INSTALL ON DEVICE
call adb install -r %TARGET%\%NAME%\bin\%NAME%-debug.apk || exit /b
pause
adb logcat


