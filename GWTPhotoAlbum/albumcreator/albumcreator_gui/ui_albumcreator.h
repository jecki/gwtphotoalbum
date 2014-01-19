/********************************************************************************
** Form generated from reading UI file 'albumcreator.ui'
**
** Created by: Qt User Interface Compiler version 5.2.0
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ALBUMCREATOR_H
#define UI_ALBUMCREATOR_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QHBoxLayout>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QLabel>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenu>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPlainTextEdit>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QSplitter>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QToolBar>
#include <QtWidgets/QVBoxLayout>
#include <QtWidgets/QWidget>
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
            AlbumCreator->setObjectName(QStringLiteral("AlbumCreator"));
        AlbumCreator->resize(860, 580);
        AlbumCreator->setMinimumSize(QSize(720, 480));
        AlbumCreator->setMaximumSize(QSize(8192, 8192));
        actionNew = new QAction(AlbumCreator);
        actionNew->setObjectName(QStringLiteral("actionNew"));
        QIcon icon;
        icon.addFile(QStringLiteral(":/:/images/filenew.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionNew->setIcon(icon);
        actionOpen = new QAction(AlbumCreator);
        actionOpen->setObjectName(QStringLiteral("actionOpen"));
        QIcon icon1;
        icon1.addFile(QStringLiteral(":/:/images/fileopen.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionOpen->setIcon(icon1);
        actionSave = new QAction(AlbumCreator);
        actionSave->setObjectName(QStringLiteral("actionSave"));
        QIcon icon2;
        icon2.addFile(QStringLiteral(":/:/images/filesave.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionSave->setIcon(icon2);
        actionSave_As = new QAction(AlbumCreator);
        actionSave_As->setObjectName(QStringLiteral("actionSave_As"));
        actionImport = new QAction(AlbumCreator);
        actionImport->setObjectName(QStringLiteral("actionImport"));
        actionExport = new QAction(AlbumCreator);
        actionExport->setObjectName(QStringLiteral("actionExport"));
        actionExit = new QAction(AlbumCreator);
        actionExit->setObjectName(QStringLiteral("actionExit"));
        QIcon icon3;
        icon3.addFile(QStringLiteral(":/:/images/exit.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionExit->setIcon(icon3);
        actionUndo = new QAction(AlbumCreator);
        actionUndo->setObjectName(QStringLiteral("actionUndo"));
        QIcon icon4;
        icon4.addFile(QStringLiteral(":/:/images/editundo.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionUndo->setIcon(icon4);
        actionRedo = new QAction(AlbumCreator);
        actionRedo->setObjectName(QStringLiteral("actionRedo"));
        QIcon icon5;
        icon5.addFile(QStringLiteral(":/:/images/editredo.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionRedo->setIcon(icon5);
        actionCopy = new QAction(AlbumCreator);
        actionCopy->setObjectName(QStringLiteral("actionCopy"));
        QIcon icon6;
        icon6.addFile(QStringLiteral(":/:/images/editcopy.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionCopy->setIcon(icon6);
        actionCuttingTool = new QAction(AlbumCreator);
        actionCuttingTool->setObjectName(QStringLiteral("actionCuttingTool"));
        QIcon icon7;
        icon7.addFile(QStringLiteral(":/:/images/editcut.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionCuttingTool->setIcon(icon7);
        actionPaste = new QAction(AlbumCreator);
        actionPaste->setObjectName(QStringLiteral("actionPaste"));
        QIcon icon8;
        icon8.addFile(QStringLiteral(":/:/images/editpaste.png"), QSize(), QIcon::Normal, QIcon::Off);
        actionPaste->setIcon(icon8);
        actionDelete = new QAction(AlbumCreator);
        actionDelete->setObjectName(QStringLiteral("actionDelete"));
        actionAbout = new QAction(AlbumCreator);
        actionAbout->setObjectName(QStringLiteral("actionAbout"));
        actionCreate_Web_Slideshow = new QAction(AlbumCreator);
        actionCreate_Web_Slideshow->setObjectName(QStringLiteral("actionCreate_Web_Slideshow"));
        actionActionCuttingTool = new QAction(AlbumCreator);
        actionActionCuttingTool->setObjectName(QStringLiteral("actionActionCuttingTool"));
        actionActionCuttingTool->setCheckable(true);
        actionActionCuttingTool->setIcon(icon7);
        centralWidget = new QWidget(AlbumCreator);
        centralWidget->setObjectName(QStringLiteral("centralWidget"));
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
        horizontalLayout->setObjectName(QStringLiteral("horizontalLayout"));
        mainSplitter = new QSplitter(centralWidget);
        mainSplitter->setObjectName(QStringLiteral("mainSplitter"));
        mainSplitter->setOrientation(Qt::Horizontal);
        layoutWidget1 = new QWidget(mainSplitter);
        layoutWidget1->setObjectName(QStringLiteral("layoutWidget1"));
        sourceImagePane = new QVBoxLayout(layoutWidget1);
        sourceImagePane->setSpacing(6);
        sourceImagePane->setContentsMargins(11, 11, 11, 11);
        sourceImagePane->setObjectName(QStringLiteral("sourceImagePane"));
        sourceImagePane->setContentsMargins(0, 0, 0, 0);
        pathSelectionPane = new QHBoxLayout();
        pathSelectionPane->setSpacing(6);
        pathSelectionPane->setObjectName(QStringLiteral("pathSelectionPane"));
        parentDirButton = new QPushButton(layoutWidget1);
        parentDirButton->setObjectName(QStringLiteral("parentDirButton"));
        parentDirButton->setMinimumSize(QSize(20, 16));
        QIcon icon9;
        icon9.addFile(QStringLiteral(":/:/images/1uparrow.png"), QSize(), QIcon::Normal, QIcon::Off);
        parentDirButton->setIcon(icon9);

        pathSelectionPane->addWidget(parentDirButton);

        pathField = new QLineEdit(layoutWidget1);
        pathField->setObjectName(QStringLiteral("pathField"));
        pathField->setMinimumSize(QSize(40, 25));

        pathSelectionPane->addWidget(pathField);


        sourceImagePane->addLayout(pathSelectionPane);

        fileBrowser = new FileBrowserListView(layoutWidget1);
        fileBrowser->setObjectName(QStringLiteral("fileBrowser"));
        fileBrowser->setMinimumSize(QSize(180, 160));
        fileBrowser->setAcceptDrops(false);
        fileBrowser->setProperty("showDropIndicator", QVariant(false));
        fileBrowser->setDragEnabled(true);
        fileBrowser->setDragDropOverwriteMode(false);
        fileBrowser->setDragDropMode(QAbstractItemView::DragDrop);
        fileBrowser->setDefaultDropAction(Qt::IgnoreAction);
        fileBrowser->setSelectionMode(QAbstractItemView::ExtendedSelection);
        fileBrowser->setIconSize(QSize(110, 70));
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
        destImagePane->setObjectName(QStringLiteral("destImagePane"));
        destImagePane->setOrientation(Qt::Vertical);
        layoutWidget2 = new QWidget(destImagePane);
        layoutWidget2->setObjectName(QStringLiteral("layoutWidget2"));
        albumBlock = new QVBoxLayout(layoutWidget2);
        albumBlock->setSpacing(6);
        albumBlock->setContentsMargins(11, 11, 11, 11);
        albumBlock->setObjectName(QStringLiteral("albumBlock"));
        albumBlock->setContentsMargins(0, 0, 0, 0);
        albumLabel = new QLabel(layoutWidget2);
        albumLabel->setObjectName(QStringLiteral("albumLabel"));
        albumLabel->setMinimumSize(QSize(200, 12));
        QFont font;
        font.setPointSize(8);
        albumLabel->setFont(font);
        albumLabel->setAlignment(Qt::AlignCenter);

        albumBlock->addWidget(albumLabel);

        albumView = new AlbumView(layoutWidget2);
        albumView->setObjectName(QStringLiteral("albumView"));
        albumView->setAcceptDrops(true);
        albumView->setFrameShape(QFrame::StyledPanel);
        albumView->setDragDropMode(QAbstractItemView::DragDrop);
        albumView->setDefaultDropAction(Qt::MoveAction);
        albumView->setSelectionMode(QAbstractItemView::ExtendedSelection);
        albumView->setIconSize(QSize(180, 160));
        albumView->setMovement(QListView::Snap);
        albumView->setFlow(QListView::TopToBottom);
        albumView->setResizeMode(QListView::Adjust);
        albumView->setLayoutMode(QListView::Batched);
        albumView->setGridSize(QSize(180, 180));
        albumView->setViewMode(QListView::IconMode);
        albumView->setUniformItemSizes(true);

        albumBlock->addWidget(albumView);

        destImagePane->addWidget(layoutWidget2);
        layoutWidget3 = new QWidget(destImagePane);
        layoutWidget3->setObjectName(QStringLiteral("layoutWidget3"));
        captionBlock = new QVBoxLayout(layoutWidget3);
        captionBlock->setSpacing(6);
        captionBlock->setContentsMargins(11, 11, 11, 11);
        captionBlock->setObjectName(QStringLiteral("captionBlock"));
        captionBlock->setContentsMargins(0, 0, 0, 0);
        originalPath = new QLabel(layoutWidget3);
        originalPath->setObjectName(QStringLiteral("originalPath"));
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
        captionWidgets->setObjectName(QStringLiteral("captionWidgets"));
        back = new QPushButton(layoutWidget3);
        back->setObjectName(QStringLiteral("back"));
        back->setMinimumSize(QSize(24, 24));
        QIcon icon10;
        icon10.addFile(QStringLiteral(":/:/images/1leftarrow.png"), QSize(), QIcon::Normal, QIcon::Off);
        back->setIcon(icon10);
        back->setFlat(false);

        captionWidgets->addWidget(back);

        captionEdit = new QPlainTextEdit(layoutWidget3);
        captionEdit->setObjectName(QStringLiteral("captionEdit"));
        QSizePolicy sizePolicy2(QSizePolicy::Preferred, QSizePolicy::Expanding);
        sizePolicy2.setHorizontalStretch(0);
        sizePolicy2.setVerticalStretch(0);
        sizePolicy2.setHeightForWidth(captionEdit->sizePolicy().hasHeightForWidth());
        captionEdit->setSizePolicy(sizePolicy2);
        captionEdit->setMinimumSize(QSize(120, 48));
        captionEdit->setBaseSize(QSize(0, 48));

        captionWidgets->addWidget(captionEdit);

        forward = new QPushButton(layoutWidget3);
        forward->setObjectName(QStringLiteral("forward"));
        forward->setMinimumSize(QSize(24, 24));
        QIcon icon11;
        icon11.addFile(QStringLiteral(":/:/images/1rightarrow.png"), QSize(), QIcon::Normal, QIcon::Off);
        forward->setIcon(icon11);

        captionWidgets->addWidget(forward);

        captionWidgets->setStretch(0, 1);
        captionWidgets->setStretch(1, 10);
        captionWidgets->setStretch(2, 1);

        captionBlock->addLayout(captionWidgets);

        cpationLabel = new QLabel(layoutWidget3);
        cpationLabel->setObjectName(QStringLiteral("cpationLabel"));
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
        menuBar->setObjectName(QStringLiteral("menuBar"));
        menuBar->setGeometry(QRect(0, 0, 860, 21));
        menuFile = new QMenu(menuBar);
        menuFile->setObjectName(QStringLiteral("menuFile"));
        menuRecently_edited = new QMenu(menuFile);
        menuRecently_edited->setObjectName(QStringLiteral("menuRecently_edited"));
        menuEdit = new QMenu(menuBar);
        menuEdit->setObjectName(QStringLiteral("menuEdit"));
        menuHelp = new QMenu(menuBar);
        menuHelp->setObjectName(QStringLiteral("menuHelp"));
        menuCreate = new QMenu(menuBar);
        menuCreate->setObjectName(QStringLiteral("menuCreate"));
        AlbumCreator->setMenuBar(menuBar);
        mainToolBar = new QToolBar(AlbumCreator);
        mainToolBar->setObjectName(QStringLiteral("mainToolBar"));
        mainToolBar->setMouseTracking(false);
        AlbumCreator->addToolBar(Qt::RightToolBarArea, mainToolBar);
        statusBar = new QStatusBar(AlbumCreator);
        statusBar->setObjectName(QStringLiteral("statusBar"));
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
        AlbumCreator->setWindowTitle(QApplication::translate("AlbumCreator", "AlbumCreator", 0));
        actionNew->setText(QApplication::translate("AlbumCreator", "New...", 0));
        actionOpen->setText(QApplication::translate("AlbumCreator", "Open...", 0));
        actionSave->setText(QApplication::translate("AlbumCreator", "Save", 0));
        actionSave_As->setText(QApplication::translate("AlbumCreator", "Save As...", 0));
        actionImport->setText(QApplication::translate("AlbumCreator", "Import...", 0));
        actionExport->setText(QApplication::translate("AlbumCreator", "Export...", 0));
        actionExit->setText(QApplication::translate("AlbumCreator", "Exit", 0));
        actionUndo->setText(QApplication::translate("AlbumCreator", "Undo", 0));
        actionRedo->setText(QApplication::translate("AlbumCreator", "Redo", 0));
        actionCopy->setText(QApplication::translate("AlbumCreator", "Copy", 0));
        actionCuttingTool->setText(QApplication::translate("AlbumCreator", "Cut", 0));
        actionPaste->setText(QApplication::translate("AlbumCreator", "Paste", 0));
        actionDelete->setText(QApplication::translate("AlbumCreator", "Delete", 0));
        actionAbout->setText(QApplication::translate("AlbumCreator", "About AlbumCreator...", 0));
        actionCreate_Web_Slideshow->setText(QApplication::translate("AlbumCreator", "Create Web Slideshow...", 0));
        actionActionCuttingTool->setText(QApplication::translate("AlbumCreator", "actionCuttingTool", 0));
#ifndef QT_NO_TOOLTIP
        actionActionCuttingTool->setToolTip(QApplication::translate("AlbumCreator", "Cut off margins or cut out a detail.", 0));
#endif // QT_NO_TOOLTIP
        actionActionCuttingTool->setShortcut(QApplication::translate("AlbumCreator", "Ctrl+D", 0));
#ifndef QT_NO_TOOLTIP
        parentDirButton->setToolTip(QString());
#endif // QT_NO_TOOLTIP
        parentDirButton->setText(QString());
        albumLabel->setText(QApplication::translate("AlbumCreator", "Album (drag pictures here)", 0));
        originalPath->setText(QApplication::translate("AlbumCreator", "pathName", 0));
        back->setText(QString());
        back->setShortcut(QApplication::translate("AlbumCreator", "Ctrl+Left", 0));
        forward->setShortcut(QApplication::translate("AlbumCreator", "Ctrl+Space", 0));
        cpationLabel->setText(QApplication::translate("AlbumCreator", "Caption (press ctrl-SPC to move to next picture):", 0));
        menuFile->setTitle(QApplication::translate("AlbumCreator", "File", 0));
        menuRecently_edited->setTitle(QApplication::translate("AlbumCreator", "Recently edited", 0));
        menuEdit->setTitle(QApplication::translate("AlbumCreator", "Edit", 0));
        menuHelp->setTitle(QApplication::translate("AlbumCreator", "Help", 0));
        menuCreate->setTitle(QApplication::translate("AlbumCreator", "Create", 0));
    } // retranslateUi

};

namespace Ui {
    class AlbumCreator: public Ui_AlbumCreator {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ALBUMCREATOR_H
