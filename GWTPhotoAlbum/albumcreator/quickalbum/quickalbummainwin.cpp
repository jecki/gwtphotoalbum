#include "quickalbummainwin.h"

#include <QFileDialog>
#include <QDir>

#include "imagecollection.h"
#include "createalbumwizzard.h"


QuickAlbumMainWin::QuickAlbumMainWin(QWidget *parent)
    : QMainWindow(parent), returnValue(0)
{
	ui.setupUi(this);
}

QuickAlbumMainWin::~QuickAlbumMainWin()
{

}


void QuickAlbumMainWin::on_startButton_clicked() {
	QFileDialog fd;
	fd.setAcceptMode(QFileDialog::AcceptOpen);
	fd.setFilter(QDir::AllEntries);
	fd.setNameFilter("*.jpg *.png");
	fd.setWindowTitle("Pick image directory or files...");
	fd.exec();
	QStringList paths = fd.selectedFiles();

	ImageCollection ic;
    CreateAlbumWizzard w(ic);
    w.setAboutTab(true);
	returnValue = w.exec();
}
