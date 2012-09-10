# -*- mode: python -*-

# This is a spec file for building a standalone executable with pyinstaller
# under linux 
#
# see http://pyinstaller.python-hosting.com for more information on pyinstaller
#
# With the present version of pyinstaller (svn co 2009-03-19) this works only with python 2.4 !

a = Analysis([os.path.join(HOMEPATH,'support/_mountzlib.py'), os.path.join(HOMEPATH,'support/unpackTK.py'), os.path.join(HOMEPATH,'support/useTK.py'), os.path.join(HOMEPATH,'support/useUnicode.py'), 'GWTPhotoAlbum/scripts/GWTPhotoAlbumCreator.py', os.path.join(HOMEPATH,'support/removeTK.py')],
             pathex=['/home/ecki/Dokumente/Software/pyinstaller'])
collect = COLLECT([('GWTPhotoAlbum-Deploy.zip', 'GWTPhotoAlbum/scripts/GWTPhotoAlbum-Deploy.zip', 'DATA')])
pyz = PYZ(a.pure)
exe = EXE(TkPKG(), pyz,
          a.scripts,
          a.binaries,
          a.zipfiles,
          a.datas + collect.toc,
          name=os.path.join('dist', 'GWTPhotoAlbumCreator'),
          debug=False,
          strip=True,
          upx=True,
          console=1 )
