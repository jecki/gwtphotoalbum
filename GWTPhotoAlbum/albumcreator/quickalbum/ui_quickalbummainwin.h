/********************************************************************************
** Form generated from reading UI file 'quickalbummainwin.ui'
**
** Created by: Qt User Interface Compiler version 5.1.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_QUICKALBUMMAINWIN_H
#define UI_QUICKALBUMMAINWIN_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QWidget>

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
            QuickAlbumMainWinClass->setObjectName(QStringLiteral("QuickAlbumMainWinClass"));
        QuickAlbumMainWinClass->resize(800, 600);
        centralwidget = new QWidget(QuickAlbumMainWinClass);
        centralwidget->setObjectName(QStringLiteral("centralwidget"));
        startButton = new QPushButton(centralwidget);
        startButton->setObjectName(QStringLiteral("startButton"));
        startButton->setGeometry(QRect(350, 230, 95, 31));
        QuickAlbumMainWinClass->setCentralWidget(centralwidget);
        menubar = new QMenuBar(QuickAlbumMainWinClass);
        menubar->setObjectName(QStringLiteral("menubar"));
        menubar->setGeometry(QRect(0, 0, 800, 29));
        QuickAlbumMainWinClass->setMenuBar(menubar);
        statusbar = new QStatusBar(QuickAlbumMainWinClass);
        statusbar->setObjectName(QStringLiteral("statusbar"));
        QuickAlbumMainWinClass->setStatusBar(statusbar);

        retranslateUi(QuickAlbumMainWinClass);

        QMetaObject::connectSlotsByName(QuickAlbumMainWinClass);
    } // setupUi

    void retranslateUi(QMainWindow *QuickAlbumMainWinClass)
    {
        QuickAlbumMainWinClass->setWindowTitle(QApplication::translate("QuickAlbumMainWinClass", "MainWindow", 0));
        startButton->setText(QApplication::translate("QuickAlbumMainWinClass", "Start...", 0));
    } // retranslateUi

};

namespace Ui {
    class QuickAlbumMainWinClass: public Ui_QuickAlbumMainWinClass {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_QUICKALBUMMAINWIN_H
