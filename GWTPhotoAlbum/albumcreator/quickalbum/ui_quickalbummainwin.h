/********************************************************************************
** Form generated from reading UI file 'quickalbummainwin.ui'
**
** Created: Sun Jan 27 22:31:35 2013
**      by: Qt User Interface Compiler version 4.8.4
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_QUICKALBUMMAINWIN_H
#define UI_QUICKALBUMMAINWIN_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QHeaderView>
#include <QtGui/QMainWindow>
#include <QtGui/QMenuBar>
#include <QtGui/QPushButton>
#include <QtGui/QStatusBar>
#include <QtGui/QWidget>

QT_BEGIN_NAMESPACE

class Ui_QuickAlbumMainWinClass
{
public:
    QWidget *centralwidget;
    QPushButton *startButton;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *QuickAlbumMainWinClass)
    {
        if (QuickAlbumMainWinClass->objectName().isEmpty())
            QuickAlbumMainWinClass->setObjectName(QString::fromUtf8("QuickAlbumMainWinClass"));
        QuickAlbumMainWinClass->resize(800, 600);
        centralwidget = new QWidget(QuickAlbumMainWinClass);
        centralwidget->setObjectName(QString::fromUtf8("centralwidget"));
        startButton = new QPushButton(centralwidget);
        startButton->setObjectName(QString::fromUtf8("startButton"));
        startButton->setGeometry(QRect(350, 230, 95, 31));
        QuickAlbumMainWinClass->setCentralWidget(centralwidget);
        menubar = new QMenuBar(QuickAlbumMainWinClass);
        menubar->setObjectName(QString::fromUtf8("menubar"));
        menubar->setGeometry(QRect(0, 0, 800, 29));
        QuickAlbumMainWinClass->setMenuBar(menubar);
        statusbar = new QStatusBar(QuickAlbumMainWinClass);
        statusbar->setObjectName(QString::fromUtf8("statusbar"));
        QuickAlbumMainWinClass->setStatusBar(statusbar);

        retranslateUi(QuickAlbumMainWinClass);

        QMetaObject::connectSlotsByName(QuickAlbumMainWinClass);
    } // setupUi

    void retranslateUi(QMainWindow *QuickAlbumMainWinClass)
    {
        QuickAlbumMainWinClass->setWindowTitle(QApplication::translate("QuickAlbumMainWinClass", "MainWindow", 0, QApplication::UnicodeUTF8));
        startButton->setText(QApplication::translate("QuickAlbumMainWinClass", "Start...", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class QuickAlbumMainWinClass: public Ui_QuickAlbumMainWinClass {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_QUICKALBUMMAINWIN_H
