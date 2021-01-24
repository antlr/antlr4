Sharing the trouble of getting github action runners to work on a mac coming straight out of the factory, running Big Sur

XCode (you need XCode to build the Swift runtime):
 - install XCode from the Mac App Store
 - Launch it, this will force installation of components
 - Go to Preferences -> Locations and select XCode as Command Line Tools

Brew (you need Brew to install maven):
  - get the script from https://brew.sh
  - once installed, run the following:
    echo 'eval $(/opt/homebrew/bin/brew shellenv)' >> /Users/{user-account}/.zprofile
    eval $(/opt/homebrew/bin/brew shellenv)
    (you need to repeat these last steps for each user account)
 
Maven (supposedly installed by the github workflow, but it's convenient to have a global install for troubleshooting):
 - brew install maven
 
JDK (we need a specific JDK):
 - download openjdk8 from Oracle (later versions break the build due to some packages having disappeared)
 - install it -> this will mess up your JAVA_HOME completely, pointing to /Library/Internet...
 - fix the JAVA_HOME mess as follows:
    sudo rm -fr /Library/Internet\ Plug-Ins/JavaAppletPlugin.plugin
    sudo rm -fr /Library/PreferencePanes/JavaControlPanel.prefpane

C++:
 - brew install cmake

C#:
 - .github/scripts/install-dotnet-on-osx.sh
    (you need to repeat this step for each user account)