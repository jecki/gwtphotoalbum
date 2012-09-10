C:\python27\python -O Configure.py
C:\python27\python Makespec.py -F -K -s -X ..\scripts\GWTPhotoAlbumCreator.py
copy ..\scripts\pyinstaller\windows_xp\GWTPhotoAlbumCreator.spec GWTPhotoAlbumCreator
C:\python27\python -O Build.py GWTPhotoAlbumCreator\GWTPhotoAlbumCreator.spec
rename GWTPhotoAlbumCreator\dist\GWTPhotoAlbumCreator.exe GWTPhotoAlbumCreator-0.7.7-win32.exe

