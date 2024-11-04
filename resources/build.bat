@echo off
cd classes
jar -cvfme ../joemvc.jar ../resources/manifest.mf joeapp.mvc.ProtoTyping joeapp/mvc/*.class ../resources/*.txt ../icons/*.png > ../log.txt
cd ..

