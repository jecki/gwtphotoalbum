#ifndef QUICKALBUMMAINWIN_H
#define QUICKALBUMMAINWIN_H

#include <QtGui/QMainWindow>
#include "ui_quickalbummainwin.h"

class QuickAlbumMainWin : public QMainWindow
{
    Q_OBJECT

public:
    int	returnValue;

    QuickAlbumMainWin(QWidget *parent = 0);
    ~QuickAlbumMainWin();

public Q_SLOTS:
	void on_startButton_clicked();

private:
    Ui::QuickAlbumMainWinClass ui;
};

#endif // QUICKALBUMMAINWIN_H
