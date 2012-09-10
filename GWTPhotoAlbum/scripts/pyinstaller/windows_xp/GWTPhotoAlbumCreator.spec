# -*- mode: python -*-
a = Analysis([os.path.join(HOMEPATH,'support\\_mountzlib.py'), os.path.join(HOMEPATH,'support\\unpackTK.py'), os.path.join(HOMEPATH,'support\\useTK.py'), os.path.join(HOMEPATH,'support\\useUnicode.py'), '../scripts/GWTPhotoAlbumCreator.py', os.path.join(HOMEPATH,'support\\removeTK.py')],
             pathex=['C:\\work\\pyinstaller'])
pyz = PYZ(a.pure)
exe = EXE(TkPKG(), pyz,
          a.scripts,
          a.binaries,
          a.zipfiles,
          a.datas,
          [('GWTPhotoAlbum-Deploy.zip', 
            '../scripts/GWTPhotoAlbum-Deploy.zip', 'DATA')],
          name=os.path.join('dist', 'GWTPhotoAlbumCreator.exe'),
          debug=False,
          strip=True,
          upx=True,
          console=True )
