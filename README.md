# SwingSane
SwingSane - A cross-platform, Scanner Access Now Easy (SANE) frontend.

## Download

The latest release can be downloaded from [swingsane.com](http://swingsane.com/).

## About

SwingSane is a cross platform, open source Java client (frontend) for [Scanner Access Now Easy (SANE)](http://www.sane-project.org/) servers (backends). SwingSane provides access to scanners connected to SANE servers on a network on any platform that supports Java and Swing. It can also be used with your own application to provide some Swing code for accessing SANE scanners.

SwingSane was originally developed as a library for [FormReturn OMR Software](https://www.formreturn.com), to provide Linux users with built-in access to scanners. You can see an example of the implemented library if you download the trial and click the scan forms button.

SwingSane was also intended to be an example of one way to use [JFreeSane](https://github.com/sjamesr/jfreesane), the core library used by SwingSane.

SwingSane is licensed under Apache 2. If you have the ability, please [help contribute](../../wiki/Contributing) to the project by adding your patches, [submitting your scanner.xml](../../wiki/scanner.xml) or just forking the project and making something better.

## Compile
To create a bin distribution, install Maven 2 and run:

* `mvn clean package`

A bin package will be created in the target directory, called swingsane(version)-bin.zip.

## Install
If you wish to install directly from the jar file, unzip the bin distribution and run the following:

* `java -jar swingsane(version).jar`

If you are using Linux, SwingSane will automatically create a Gnome desktop launcher file for your convenience.

## Video Tutorials
Video tutorials can be found on the [SwingSane youtube channel](https://www.youtube.com/channel/UC-WXALbc1ZnhpsUlvOgSqdg).

## SwingSane Wiki
The [SwingSane Wiki](../../wiki/Home) contains further information about the project and contributing to it.
