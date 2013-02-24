/********************************************************************************
** Form generated from reading UI file 'albumcreator.ui'
**
** Created: Sun Jan 27 22:31:05 2013
**      by: Qt User Interface Compiler version 4.8.4
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ALBUMCREATOR_H
#define UI_ALBUMCREATOR_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QHBoxLayout>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QLineEdit>
#include <QtGui/QMainWindow>
#include <QtGui/QMenu>
#include <QtGui/QMenuBar>
#include <QtGui/QPlainTextEdit>
#include <QtGui/QPushButton>
#include <QtGui/QSplitter>
#include <QtGui/QStatusBar>
#include <QtGui/QToolBar>
#include <QtGui/QVBoxLayout>
#include <QtGui/QWidget>
#include <albumview.h>
#include <filebrowserlistview.h>

QT_BEGIN_NAMESPACE

class Ui_AlbumCreator
{
public:
    QAction *actionNew;
    QAction *actionOpen;
    QAction *actionSave;
    QAction *actionSave_As;
    QAction *actionImport;
    QAction *actionExport;
    QAction *actionExit;
    QAction *actionUndo;
    QAction *actionRedo;
    QAction *actionCopy;
    QAction *actionCuttingTool;
    QAction *actionPaste;
    QAction *actionDelete;
    QAction *actionAbout;
    QAction *actionCreate_Web_Slideshow;
    QAction *actionActionCuttingTool;
    QWidget *centralWidget;
    QHBoxLayout *horizontalLayout;
    QSplitter *mainSplitter;
    QWidget *layoutWidget1;
    QVBoxLayout *sourceImagePane;
    QHBoxLayout *pathSelectionPane;
    QPushButton *parentDirButton;
    QLineEdit *pathField;
    FileBrowserListView *fileBrowser;
    QSplitter *destImagePane;
    QWidget *layoutWidget2;
    QVBoxLayout *albumBlock;
    QLabel *albumLabel;
    AlbumView *albumView;
    QWidget *layoutWidget3;
    QVBoxLayout *captionBlock;
    QLabel *originalPath;
    QHBoxLayout *captionWidgets;
    QPushButton *back;
    QPlainTextEdit *captionEdit;
    QPushButton *forward;
    QLabel *cpationLabel;
    QMenuBar *menuBar;
    QMenu *menuFile;
    QMenu *menuRecently_edited;
    QMenu *menuEdit;
    QMenu *menuHelp;
    QMenu *menuCreate;
    QToolBar *mainToolBar;
    QStatusBar *statusBar;

    void setupUi(QMainWindow *AlbumCreator)
    {
        if (AlbumCreator->objectName().isEmpty())
            AlbumCreator->setObjectName(QString::fromUtf8("AlbumCreator"));
        AlbumCreator->resize(860, 580);
        AlbumCreator->setMinimumSize(QSize(720, 480));
        AlbumCreator->setMaximumSize(QSize(8192, 8192));
        actionNew = new QAction(AlbumCreator);
        actionNew->setObjectName(QString::fromUtf8("actionNew"));
        QIcon icon;
        icon.addFile(QString::fromUtf8(":/:/images/filenew.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionNew->setIcon(icon);
        actionOpen = new QAction(AlbumCreator);
        actionOpen->setObjectName(QString::fromUtf8("actionOpen"));
        QIcon icon1;
        icon1.addFile(QString::fromUtf8(":/:/images/fileopen.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionOpen->setIcon(icon1);
        actionSave = new QAction(AlbumCreator);
        actionSave->setObjectName(QString::fromUtf8("actionSave"));
        QIcon icon2;
        icon2.addFile(QString::fromUtf8(":/:/images/filesave.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionSave->setIcon(icon2);
        actionSave_As = new QAction(AlbumCreator);
        actionSave_As->setObjectName(QString::fromUtf8("actionSave_As"));
        actionImport = new QAction(AlbumCreator);
        actionImport->setObjectName(QString::fromUtf8("actionImport"));
        actionExport = new QAction(AlbumCreator);
        actionExport->setObjectName(QString::fromUtf8("actionExport"));
        actionExit = new QAction(AlbumCreator);
        actionExit->setObjectName(QString::fromUtf8("actionExit"));
        QIcon icon3;
        icon3.addFile(QString::fromUtf8(":/:/images/exit.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionExit->setIcon(icon3);
        actionUndo = new QAction(AlbumCreator);
        actionUndo->setObjectName(QString::fromUtf8("actionUndo"));
        QIcon icon4;
        icon4.addFile(QString::fromUtf8(":/:/images/editundo.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionUndo->setIcon(icon4);
        actionRedo = new QAction(AlbumCreator);
        actionRedo->setObjectName(QString::fromUtf8("actionRedo"));
        QIcon icon5;
        icon5.addFile(QString::fromUtf8(":/:/images/editredo.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionRedo->setIcon(icon5);
        actionCopy = new QAction(AlbumCreator);
        actionCopy->setObjectName(QString::fromUtf8("actionCopy"));
        QIcon icon6;
        icon6.addFile(QString::fromUtf8(":/:/images/editcopy.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionCopy->setIcon(icon6);
        actionCuttingTool = new QAction(AlbumCreator);
        actionCuttingTool->setObjectName(QString::fromUtf8("actionCuttingTool"));
        QIcon icon7;
        icon7.addFile(QString::fromUtf8(":/:/images/editcut.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionCuttingTool->setIcon(icon7);
        actionPaste = new QAction(AlbumCreator);
        actionPaste->setObjectName(QString::fromUtf8("actionPaste"));
        QIcon icon8;
        icon8.addFile(QString::fromUtf8(":/:/images/editpaste.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionPaste->setIcon(icon8);
        actionDelete = new QAction(AlbumCreator);
        actionDelete->setObjectName(QString::fromUtf8("actionDelete"));
        actionAbout = new QAction(AlbumCreator);
        actionAbout->setObjectName(QString::fromUtf8("actionAbout"));
        actionCreate_Web_Slideshow = new QAction(AlbumCreator);
        actionCreate_Web_Slideshow->setObjectName(QString::fromUtf8("actionCreate_Web_Slideshow"));
        actionActionCuttingTool = new QAction(AlbumCreator);
        actionActionCuttingTool->setObjectName(QString::fromUtf8("actionActionCuttingTool"));
        actionActionCuttingTool->setCheckable(true);
        actionActionCuttingTool->setIcon(icon7);
        centralWidget = new QWidget(AlbumCreator);
        centralWidget->setObjectName(QString::fromUtf8("centralWidget"));
        QSizePolicy sizePolicy(QSizePolicy::MinimumExpanding, QSizePolicy::MinimumExpanding);
        sizePolicy.setHorizontalStretch(0);
        sizePolicy.setVerticalStretch(0);
        sizePolicy.setHeightForWidth(centralWidget->sizePolicy().hasHeightForWidth());
        centralWidget->setSizePolicy(sizePolicy);
        centralWidget->setMinimumSize(QSize(640, 400));
        centralWidget->setMaximumSize(QSize(8192, 6134));
        horizontalLayout = new QHBoxLayout(centralWidget);
        horizontalLayout->setSpacing(6);
        horizontalLayout->setContentsMargins(11, 11, 11, 11);
        horizontalLayout->setObjectName(QString::fromUtf8("horizontalLayout"));
        mainSplitter = new QSplitter(centralWidget);
        mainSplitter->setObjectName(QString::fromUtf8("mainSplitter"));
        mainSplitter->setOrientation(Qt::Horizontal);
        layoutWidget1 = new QWidget(mainSplitter);
        layoutWidget1->setObjectName(QString::fromUtf8("layoutWidget1"));
        sourceImagePane = new QVBoxLayout(layoutWidget1);
        sourceImagePane->setSpacing(6);
        sourceImagePane->setContentsMargins(11, 11, 11, 11);
        sourceImagePane->setObjectName(QString::fromUtf8("sourceImagePane"));
        sourceImagePane->setContentsMargins(0, 0, 0, 0);
        pathSelectionPane = new QHBoxLayout();
        pathSelectionPane->setSpacing(6);
        pathSelectionPane->setObjectName(QString::fromUtf8("pathSelectionPane"));
        parentDirButton = new QPushButton(layoutWidget1);
        parentDirButton->setObjectName(QString::fromUtf8("parentDirButton"));
        parentDirButton->setMinimumSize(QSize(20, 16));
        QIcon icon9;
        icon9.addFile(QString::fromUtf8(":/:/images/1uparrow.png"), QSize(), QIcon::Normal, QIcon::Off);
        parentDirButton->setIcon(icon9);

        pathSelectionPane->addWidget(parentDirButton);

        pathField = new QLineEdit(layoutWidget1);
        pathField->setObjectName(QString::fromUtf8("pathField"));
        pathField->setMinimumSize(QSize(40, 25));

        pathSelectionPane->addWidget(pathField);


        sourceImagePane->addLayout(pathSelectionPane);

        fileBrowser = new FileBrowserListView(layoutWidget1);
        fileBrowser->setObjectName(QString::fromUtf8("fileBrowser"));
        fileBrowser->setMinimumSize(QSize(180, 160));
        fileBrowser->setAcceptDrops(false);
        fileBrowser->setProperty("showDropIndicator", QVariant(false));
        fileBrowser->setDragEnabled(true);
        fileBrowser->setDragDropOverwriteMode(false);
        fileBrowser->setDragDropMode(QAbstractItemView::DragDrop);
        fileBrowser->setDefaultDropAction(Qt::IgnoreAction);
        fileBrowser->setSelectionMode(QAbstractItemView::ExtendedSelection);
        fileBrowser->setIconSize(QSize(100, 75));
        fileBrowser->setMovement(QListView::Snap);
        fileBrowser->setResizeMode(QListView::Adjust);
        fileBrowser->setLayoutMode(QListView::Batched);
        fileBrowser->setSpacing(2);
        fileBrowser->setGridSize(QSize(120, 100));
        fileBrowser->setViewMode(QListView::IconMode);
        fileBrowser->setUniformItemSizes(true);

        sourceImagePane->addWidget(fileBrowser);

        mainSplitter->addWidget(layoutWidget1);
        destImagePane = new QSplitter(mainSplitter);
        destImagePane->setObjectName(QString::fromUtf8("destImagePane"));
        destImagePane->setOrientation(Qt::Vertical);
        layoutWidget2 = new QWidget(destImagePane);
        layoutWidget2->setObjectName(QString::fromUtf8("layoutWidget2"));
        albumBlock = new QVBoxLayout(layoutWidget2);
        albumBlock->setSpacing(6);
        albumBlock->setContentsMargins(11, 11, 11, 11);
        albumBlock->setObjectName(QString::fromUtf8("albumBlock"));
        albumBlock->setContentsMargins(0, 0, 0, 0);
        albumLabel = new QLabel(layoutWidget2);
        albumLabel->setObjectName(QString::fromUtf8("albumLabel"));
        albumLabel->setMinimumSize(QSize(200, 12));
        QFont font;
        font.setPointSize(8);
        albumLabel->setFont(font);
        albumLabel->setAlignment(Qt::AlignCenter);

        albumBlock->addWidget(albumLabel);

        albumView = new AlbumView(layoutWidget2);
        albumView->setObjectName(QString::fromUtf8("albumView"));
        albumView->setAcceptDrops(true);
        albumView->setDragDropMode(QAbstractItemView::DragDrop);
        albumView->setDefaultDropAction(Qt::MoveAction);
        albumView->setSelectionMode(QAbstractItemView::ExtendedSelection);
        albumView->setIconSize(QSize(160, 160));
        albumView->setMovement(QListView::Snap);
        albumView->setResizeMode(QListView::Adjust);
        albumView->setLayoutMode(QListView::Batched);
        albumView->setGridSize(QSize(180, 180));
        albumView->setViewMode(QListView::IconMode);

        albumBlock->addWidget(albumView);

        destImagePane->addWidget(layoutWidget2);
        layoutWidget3 = new QWidget(destImagePane);
        layoutWidget3->setObjectName(QString::fromUtf8("layoutWidget3"));
        captionBlock = new QVBoxLayout(layoutWidget3);
        captionBlock->setSpacing(6);
        captionBlock->setContentsMargins(11, 11, 11, 11);
        captionBlock->setObjectName(QString::fromUtf8("captionBlock"));
        captionBlock->setContentsMargins(0, 0, 0, 0);
        originalPath = new QLabel(layoutWidget3);
        originalPath->setObjectName(QString::fromUtf8("originalPath"));
        QSizePolicy sizePolicy1(QSizePolicy::Expanding, QSizePolicy::Fixed);
        sizePolicy1.setHorizontalStretch(0);
        sizePolicy1.setVerticalStretch(0);
        sizePolicy1.setHeightForWidth(originalPath->sizePolicy().hasHeightForWidth());
        originalPath->setSizePolicy(sizePolicy1);
        originalPath->setMinimumSize(QSize(80, 12));
        originalPath->setTextFormat(Qt::PlainText);
        originalPath->setAlignment(Qt::AlignCenter);

        captionBlock->addWidget(originalPath);

        captionWidgets = new QHBoxLayout();
        captionWidgets->setSpacing(6);
        captionWidgets->setObjectName(QString::fromUtf8("captionWidgets"));
        back = new QPushButton(layoutWidget3);
        back->setObjectName(QString::fromUtf8("back"));
        back->setMinimumSize(QSize(24, 24));
        QIcon icon10;
        icon10.addFile(QString::fromUtf8(":/:/images/1leftarrow.png"), QSize(), QIcon::Normal, QIcon::Off);
        back->setIcon(icon10);
        back->setFlat(false);

        captionWidgets->addWidget(back);

        captionEdit = new QPlainTextEdit(layoutWidget3);
        captionEdit->setObjectName(QString::fromUtf8("captionEdit"));
        QSizePolicy sizePolicy2(QSizePolicy::Preferred, QSizePolicy::Expanding);
        sizePolicy2.setHorizontalStretch(0);
        sizePolicy2.setVerticalStretch(0);
        sizePolicy2.setHeightForWidth(captionEdit->sizePolicy().hasHeightForWidth());
        captionEdit->setSizePolicy(sizePolicy2);
        captionEdit->setMinimumSize(QSize(120, 48));
        captionEdit->setBaseSize(QSize(0, 48));

        captionWidgets->addWidget(captionEdit);

        forward = new QPushButton(layoutWidget3);
        forward->setObjectName(QString::fromUtf8("forward"));
        forward->setMinimumSize(QSize(24, 24));
        QIcon icon11;
        icon11.addFile(QString::fromUtf8(":/:/images/1rightarrow.png"), QSize(), QIcon::Normal, QIcon::Off);
        forward->setIcon(icon11);

        captionWidgets->addWidget(forward);

        captionWidgets->setStretch(0, 1);
        captionWidgets->setStretch(1, 10);
        captionWidgets->setStretch(2, 1);

        captionBlock->addLayout(captionWidgets);

        cpationLabel = new QLabel(layoutWidget3);
        cpationLabel->setObjectName(QString::fromUtf8("cpationLabel"));
        sizePolicy1.setHeightForWidth(cpationLabel->sizePolicy().hasHeightForWidth());
        cpationLabel->setSizePolicy(sizePolicy1);
        cpationLabel->setMinimumSize(QSize(200, 12));
        QFont font1;
        font1.setPointSize(8);
        font1.setUnderline(false);
        font1.setKerning(false);
        cpationLabel->setFont(font1);
        cpationLabel->setAlignment(Qt::AlignCenter);

        captionBlock->addWidget(cpationLabel);

        captionBlock->setStretch(0, 1);
        captionBlock->setStretch(1, 3);
        destImagePane->addWidget(layoutWidget3);
        mainSplitter->addWidget(destImagePane);

        horizontalLayout->addWidget(mainSplitter);

        AlbumCreator->setCentralWidget(centralWidget);
        menuBar = new QMenuBar(AlbumCreator);
        menuBar->setObjectName(QString::fromUtf8("menuBar"));
        menuBar->setGeometry(QRect(0, 0, 860, 22));
        menuFile = new QMenu(menuBar);
        menuFile->setObjectName(QString::fromUtf8("menuFile"));
        menuRecently_edited = new QMenu(menuFile);
        menuRecently_edited->setObjectName(QString::fromUtf8("menuRecently_edited"));
        menuEdit = new QMenu(menuBar);
        menuEdit->setObjectName(QString::fromUtf8("menuEdit"));
        menuHelp = new QMenu(menuBar);
        menuHelp->setObjectName(QString::fromUtf8("menuHelp"));
        menuCreate = new QMenu(menuBar);
        menuCreate->setObjectName(QString::fromUtf8("menuCreate"));
        AlbumCreator->setMenuBar(menuBar);
        mainToolBar = new QToolBar(AlbumCreator);
        mainToolBar->setObjectName(QString::fromUtf8("mainToolBar"));
        mainToolBar->setMouseTracking(false);
        AlbumCreator->addToolBar(Qt::RightToolBarArea, mainToolBar);
        statusBar = new QStatusBar(AlbumCreator);
        statusBar->setObjectName(QString::fromUtf8("statusBar"));
        AlbumCreator->setStatusBar(statusBar);

        menuBar->addAction(menuFile->menuAction());
        menuBar->addAction(menuEdit->menuAction());
        menuBar->addAction(menuCreate->menuAction());
        menuBar->addAction(menuHelp->menuAction());
        menuFile->addAction(actionNew);
        menuFile->addAction(actionOpen);
        menuFile->addAction(menuRecently_edited->menuAction());
        menuFile->addSeparator();
        menuFile->addAction(actionSave);
        menuFile->addAction(actionSave_As);
        menuFile->addSeparator();
        menuFile->addAction(actionImport);
        menuFile->addAction(actionExport);
        menuFile->addSeparator();
        menuFile->addAction(actionExit);
        menuRecently_edited->addSeparator();
        menuEdit->addAction(actionUndo);
        menuEdit->addAction(actionRedo);
        menuEdit->addSeparator();
        menuEdit->addAction(actionCuttingTool);
        menuEdit->addAction(actionCopy);
        menuEdit->addAction(actionPaste);
        menuEdit->addAction(actionDelete);
        menuHelp->addAction(actionAbout);
        menuCreate->addAction(actionCreate_Web_Slideshow);
        mainToolBar->addAction(actionCuttingTool);
        mainToolBar->addSeparator();
        mainToolBar->addAction(actionUndo);
        mainToolBar->addAction(actionRedo);
        mainToolBar->addSeparator();
        mainToolBar->addAction(actionSave);

        retranslateUi(AlbumCreator);

        QMetaObject::connectSlotsByName(AlbumCreator);
    } // setupUi

    void retranslateUi(QMainWindow *AlbumCreator)
    {
        AlbumCreator->setWindowTitle(QApplication::translate("AlbumCreator", "AlbumCreator", 0, QApplication::UnicodeUTF8));
        actionNew->setText(QApplication::translate("AlbumCreator", "New...", 0, QApplication::UnicodeUTF8));
        actionOpen->setText(QApplication::translate("AlbumCreator", "Open...", 0, QApplication::UnicodeUTF8));
        actionSave->setText(QApplication::translate("AlbumCreator", "Save", 0, QApplication::UnicodeUTF8));
        actionSave_As->setText(QApplication::translate("AlbumCreator", "Save As...", 0, QApplication::UnicodeUTF8));
        actionImport->setText(QApplication::translate("AlbumCreator", "Import...", 0, QApplication::UnicodeUTF8));
        actionExport->setText(QApplication::translate("AlbumCreator", "Export...", 0, QApplication::UnicodeUTF8));
        actionExit->setText(QApplication::translate("AlbumCreator", "Exit", 0, QApplication::UnicodeUTF8));
        actionUndo->setText(QApplication::translate("AlbumCreator", "Undo", 0, QApplication::UnicodeUTF8));
        actionRedo->setText(QApplication::translate("AlbumCreator", "Redo", 0, QApplication::UnicodeUTF8));
        actionCopy->setText(QApplication::translate("AlbumCreator", "Copy", 0, QApplication::UnicodeUTF8));
        actionCuttingTool->setText(QApplication::translate("AlbumCreator", "Cut", 0, QApplication::UnicodeUTF8));
        actionPaste->setText(QApplication::translate("AlbumCreator", "Paste", 0, QApplication::UnicodeUTF8));
        actionDelete->setText(QApplication::translate("AlbumCreator", "Delete", 0, QApplication::UnicodeUTF8));
        actionAbout->setText(QApplication::translate("AlbumCreator", "About AlbumCreator...", 0, QApplication::UnicodeUTF8));
        actionCreate_Web_Slideshow->setText(QApplication::translate("AlbumCreator", "Create Web Slideshow...", 0, QApplication::UnicodeUTF8));
        actionActionCuttingTool->setText(QApplication::translate("AlbumCreator", "actionCuttingTool", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        actionActionCuttingTool->setToolTip(QApplication::translate("AlbumCreator", "Cut off margins or cut out a detail.", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        actionActionCuttingTool->setShortcut(QApplication::translate("AlbumCreator", "Ctrl+D", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        parentDirButton->setToolTip(QString());
#endif // QT_NO_TOOLTIP
        parentDirButton->setText(QString());
        albumLabel->setText(QApplication::translate("AlbumCreator", "Album (drag pictures here)", 0, QApplication::UnicodeUTF8));
        originalPath->setText(QApplication::translate("AlbumCreator", "pathName", 0, QApplication::UnicodeUTF8));
        back->setText(QString());
        back->setShortcut(QApplication::translate("AlbumCreator", "Ctrl+Left", 0, QApplication::UnicodeUTF8));
        forward->setShortcut(QApplication::translate("AlbumCreator", "Ctrl+Space", 0, QApplication::UnicodeUTF8));
        cpationLabel->setText(QApplication::translate("AlbumCreator", "Caption (press ctrl-SPC to move to next picture):", 0, QApplication::UnicodeUTF8));
        menuFile->setTitle(QApplication::translate("AlbumCreator", "File", 0, QApplication::UnicodeUTF8));
        menuRecently_edited->setTitle(QApplication::translate("AlbumCreator", "Recently edited", 0, QApplication::UnicodeUTF8));
        menuEdit->setTitle(QApplication::translate("AlbumCreator", "Edit", 0, QApplication::UnicodeUTF8));
        menuHelp->setTitle(QApplication::translate("AlbumCreator", "Help", 0, QApplication::UnicodeUTF8));
        menuCreate->setTitle(QApplication::translate("AlbumCreator", "Create", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class AlbumCreator: public Ui_AlbumCreator {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ALBUMCREATOR_H
