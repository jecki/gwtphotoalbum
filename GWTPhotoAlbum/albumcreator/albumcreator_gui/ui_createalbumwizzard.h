/********************************************************************************
** Form generated from reading UI file 'createalbumwizzard.ui'
**
** Created: Sun Jan 27 22:31:05 2013
**      by: Qt User Interface Compiler version 4.8.4
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_CREATEALBUMWIZZARD_H
#define UI_CREATEALBUMWIZZARD_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QCheckBox>
#include <QtGui/QComboBox>
#include <QtGui/QDialog>
#include <QtGui/QFrame>
#include <QtGui/QHBoxLayout>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QLineEdit>
#include <QtGui/QProgressBar>
#include <QtGui/QPushButton>
#include <QtGui/QRadioButton>
#include <QtGui/QSlider>
#include <QtGui/QSpacerItem>
#include <QtGui/QTabWidget>
#include <QtGui/QTextBrowser>
#include <QtGui/QTextEdit>
#include <QtGui/QWidget>

QT_BEGIN_NAMESPACE

class Ui_CreateAlbumWizzardClass
{
public:
    QTabWidget *tabWidget;
    QWidget *infoTab;
    QFrame *galleryPageFrrame;
    QCheckBox *addGallery;
    QLabel *titleLable;
    QLabel *subTitleLabel;
    QLineEdit *titleEdit;
    QLineEdit *subTitleEdit;
    QLabel *bottomLineLabel;
    QTextEdit *bottomLineEdit;
    QWidget *configurationTab;
    QFrame *addArchiveFrame;
    QCheckBox *archiveCheckBox;
    QLabel *archiveResLabel;
    QComboBox *archiveRes;
    QFrame *navigationTypeFrame;
    QLabel *navigationLabel;
    QRadioButton *controlPanelNavigation;
    QRadioButton *filmstripNavigation;
    QFrame *imageCompressionFrame;
    QLabel *imageCompressionLabel;
    QSlider *compressionSlider;
    QLabel *compresssionLabel;
    QFrame *imageChangeFrame;
    QLabel *label;
    QRadioButton *fadeInOut;
    QRadioButton *crossFade;
    QWidget *destinationTab;
    QLabel *protocolLabel;
    QComboBox *protocolSelector;
    QLabel *locationLabel;
    QLineEdit *locationEdit;
    QPushButton *locationButton;
    QLabel *portLabel;
    QLineEdit *portEdit;
    QLabel *usernameLabel;
    QLineEdit *usernameEdit;
    QLabel *passwordLabel;
    QLineEdit *passwordEdit;
    QWidget *createTab;
    QProgressBar *progress;
    QPushButton *startButton;
    QLabel *labelCreateGallery;
    QWidget *horizontalLayoutWidget;
    QHBoxLayout *openBrowserLayout;
    QSpacerItem *openBrowserLeftSpacer;
    QCheckBox *openBrowserCheckBox;
    QSpacerItem *openBrowserRightSpacer;
    QWidget *aboutTab;
    QTextBrowser *aboutTextBrowser;
    QPushButton *nextButton;
    QPushButton *cancelButton;
    QPushButton *prevButton;

    void setupUi(QDialog *CreateAlbumWizzardClass)
    {
        if (CreateAlbumWizzardClass->objectName().isEmpty())
            CreateAlbumWizzardClass->setObjectName(QString::fromUtf8("CreateAlbumWizzardClass"));
        CreateAlbumWizzardClass->resize(592, 380);
        tabWidget = new QTabWidget(CreateAlbumWizzardClass);
        tabWidget->setObjectName(QString::fromUtf8("tabWidget"));
        tabWidget->setEnabled(true);
        tabWidget->setGeometry(QRect(0, 10, 600, 291));
        tabWidget->setTabPosition(QTabWidget::North);
        tabWidget->setTabShape(QTabWidget::Rounded);
        infoTab = new QWidget();
        infoTab->setObjectName(QString::fromUtf8("infoTab"));
        galleryPageFrrame = new QFrame(infoTab);
        galleryPageFrrame->setObjectName(QString::fromUtf8("galleryPageFrrame"));
        galleryPageFrrame->setGeometry(QRect(20, 20, 551, 221));
        galleryPageFrrame->setFrameShape(QFrame::NoFrame);
        galleryPageFrrame->setFrameShadow(QFrame::Raised);
        addGallery = new QCheckBox(galleryPageFrrame);
        addGallery->setObjectName(QString::fromUtf8("addGallery"));
        addGallery->setGeometry(QRect(10, 10, 181, 22));
        addGallery->setChecked(true);
        titleLable = new QLabel(galleryPageFrrame);
        titleLable->setObjectName(QString::fromUtf8("titleLable"));
        titleLable->setGeometry(QRect(30, 50, 121, 17));
        subTitleLabel = new QLabel(galleryPageFrrame);
        subTitleLabel->setObjectName(QString::fromUtf8("subTitleLabel"));
        subTitleLabel->setGeometry(QRect(30, 90, 121, 17));
        titleEdit = new QLineEdit(galleryPageFrrame);
        titleEdit->setObjectName(QString::fromUtf8("titleEdit"));
        titleEdit->setGeometry(QRect(170, 50, 371, 27));
        titleEdit->setMaxLength(1024);
        subTitleEdit = new QLineEdit(galleryPageFrrame);
        subTitleEdit->setObjectName(QString::fromUtf8("subTitleEdit"));
        subTitleEdit->setGeometry(QRect(170, 90, 371, 27));
        subTitleEdit->setMaxLength(1024);
        bottomLineLabel = new QLabel(galleryPageFrrame);
        bottomLineLabel->setObjectName(QString::fromUtf8("bottomLineLabel"));
        bottomLineLabel->setGeometry(QRect(30, 140, 121, 17));
        bottomLineEdit = new QTextEdit(galleryPageFrrame);
        bottomLineEdit->setObjectName(QString::fromUtf8("bottomLineEdit"));
        bottomLineEdit->setGeometry(QRect(170, 140, 371, 61));
        bottomLineEdit->setAcceptRichText(false);
        tabWidget->addTab(infoTab, QString());
        configurationTab = new QWidget();
        configurationTab->setObjectName(QString::fromUtf8("configurationTab"));
        addArchiveFrame = new QFrame(configurationTab);
        addArchiveFrame->setObjectName(QString::fromUtf8("addArchiveFrame"));
        addArchiveFrame->setGeometry(QRect(20, 20, 551, 41));
        addArchiveFrame->setFrameShape(QFrame::NoFrame);
        addArchiveFrame->setFrameShadow(QFrame::Raised);
        archiveCheckBox = new QCheckBox(addArchiveFrame);
        archiveCheckBox->setObjectName(QString::fromUtf8("archiveCheckBox"));
        archiveCheckBox->setGeometry(QRect(10, 10, 261, 22));
        archiveCheckBox->setChecked(false);
        archiveResLabel = new QLabel(addArchiveFrame);
        archiveResLabel->setObjectName(QString::fromUtf8("archiveResLabel"));
        archiveResLabel->setGeometry(QRect(280, 10, 121, 20));
        archiveResLabel->setAlignment(Qt::AlignLeading|Qt::AlignLeft|Qt::AlignVCenter);
        archiveRes = new QComboBox(addArchiveFrame);
        archiveRes->setObjectName(QString::fromUtf8("archiveRes"));
        archiveRes->setGeometry(QRect(407, 10, 131, 27));
        navigationTypeFrame = new QFrame(configurationTab);
        navigationTypeFrame->setObjectName(QString::fromUtf8("navigationTypeFrame"));
        navigationTypeFrame->setGeometry(QRect(20, 80, 551, 41));
        navigationTypeFrame->setFrameShape(QFrame::NoFrame);
        navigationTypeFrame->setFrameShadow(QFrame::Raised);
        navigationLabel = new QLabel(navigationTypeFrame);
        navigationLabel->setObjectName(QString::fromUtf8("navigationLabel"));
        navigationLabel->setGeometry(QRect(10, 10, 151, 17));
        controlPanelNavigation = new QRadioButton(navigationTypeFrame);
        controlPanelNavigation->setObjectName(QString::fromUtf8("controlPanelNavigation"));
        controlPanelNavigation->setGeometry(QRect(350, 10, 121, 22));
        filmstripNavigation = new QRadioButton(navigationTypeFrame);
        filmstripNavigation->setObjectName(QString::fromUtf8("filmstripNavigation"));
        filmstripNavigation->setGeometry(QRect(170, 10, 106, 22));
        filmstripNavigation->setChecked(true);
        imageCompressionFrame = new QFrame(configurationTab);
        imageCompressionFrame->setObjectName(QString::fromUtf8("imageCompressionFrame"));
        imageCompressionFrame->setGeometry(QRect(20, 200, 551, 41));
        imageCompressionFrame->setFrameShape(QFrame::NoFrame);
        imageCompressionFrame->setFrameShadow(QFrame::Raised);
        imageCompressionLabel = new QLabel(imageCompressionFrame);
        imageCompressionLabel->setObjectName(QString::fromUtf8("imageCompressionLabel"));
        imageCompressionLabel->setGeometry(QRect(10, 10, 151, 17));
        compressionSlider = new QSlider(imageCompressionFrame);
        compressionSlider->setObjectName(QString::fromUtf8("compressionSlider"));
        compressionSlider->setGeometry(QRect(170, 10, 311, 20));
        compressionSlider->setMinimum(50);
        compressionSlider->setMaximum(98);
        compressionSlider->setValue(90);
        compressionSlider->setOrientation(Qt::Horizontal);
        compresssionLabel = new QLabel(imageCompressionFrame);
        compresssionLabel->setObjectName(QString::fromUtf8("compresssionLabel"));
        compresssionLabel->setGeometry(QRect(490, 10, 51, 20));
        compresssionLabel->setAlignment(Qt::AlignRight|Qt::AlignTrailing|Qt::AlignVCenter);
        imageChangeFrame = new QFrame(configurationTab);
        imageChangeFrame->setObjectName(QString::fromUtf8("imageChangeFrame"));
        imageChangeFrame->setGeometry(QRect(20, 140, 551, 41));
        imageChangeFrame->setFrameShape(QFrame::NoFrame);
        imageChangeFrame->setFrameShadow(QFrame::Raised);
        imageChangeFrame->setLineWidth(1);
        label = new QLabel(imageChangeFrame);
        label->setObjectName(QString::fromUtf8("label"));
        label->setGeometry(QRect(10, 10, 101, 21));
        fadeInOut = new QRadioButton(imageChangeFrame);
        fadeInOut->setObjectName(QString::fromUtf8("fadeInOut"));
        fadeInOut->setGeometry(QRect(170, 10, 151, 26));
        fadeInOut->setChecked(true);
        crossFade = new QRadioButton(imageChangeFrame);
        crossFade->setObjectName(QString::fromUtf8("crossFade"));
        crossFade->setGeometry(QRect(350, 10, 108, 26));
        tabWidget->addTab(configurationTab, QString());
        destinationTab = new QWidget();
        destinationTab->setObjectName(QString::fromUtf8("destinationTab"));
        protocolLabel = new QLabel(destinationTab);
        protocolLabel->setObjectName(QString::fromUtf8("protocolLabel"));
        protocolLabel->setGeometry(QRect(20, 40, 91, 21));
        protocolLabel->setAlignment(Qt::AlignLeading|Qt::AlignLeft|Qt::AlignVCenter);
        protocolSelector = new QComboBox(destinationTab);
        protocolSelector->setObjectName(QString::fromUtf8("protocolSelector"));
        protocolSelector->setGeometry(QRect(120, 40, 161, 25));
        locationLabel = new QLabel(destinationTab);
        locationLabel->setObjectName(QString::fromUtf8("locationLabel"));
        locationLabel->setGeometry(QRect(20, 100, 91, 21));
        locationLabel->setAlignment(Qt::AlignLeading|Qt::AlignLeft|Qt::AlignVCenter);
        locationEdit = new QLineEdit(destinationTab);
        locationEdit->setObjectName(QString::fromUtf8("locationEdit"));
        locationEdit->setEnabled(true);
        locationEdit->setGeometry(QRect(120, 100, 411, 21));
        locationButton = new QPushButton(destinationTab);
        locationButton->setObjectName(QString::fromUtf8("locationButton"));
        locationButton->setGeometry(QRect(540, 100, 31, 27));
        portLabel = new QLabel(destinationTab);
        portLabel->setObjectName(QString::fromUtf8("portLabel"));
        portLabel->setEnabled(false);
        portLabel->setGeometry(QRect(380, 150, 51, 21));
        portEdit = new QLineEdit(destinationTab);
        portEdit->setObjectName(QString::fromUtf8("portEdit"));
        portEdit->setEnabled(false);
        portEdit->setGeometry(QRect(440, 150, 91, 21));
        portEdit->setMaxLength(15);
        usernameLabel = new QLabel(destinationTab);
        usernameLabel->setObjectName(QString::fromUtf8("usernameLabel"));
        usernameLabel->setEnabled(false);
        usernameLabel->setGeometry(QRect(20, 150, 91, 21));
        usernameEdit = new QLineEdit(destinationTab);
        usernameEdit->setObjectName(QString::fromUtf8("usernameEdit"));
        usernameEdit->setEnabled(false);
        usernameEdit->setGeometry(QRect(120, 150, 201, 21));
        usernameEdit->setMaxLength(255);
        passwordLabel = new QLabel(destinationTab);
        passwordLabel->setObjectName(QString::fromUtf8("passwordLabel"));
        passwordLabel->setEnabled(false);
        passwordLabel->setGeometry(QRect(20, 200, 91, 21));
        passwordEdit = new QLineEdit(destinationTab);
        passwordEdit->setObjectName(QString::fromUtf8("passwordEdit"));
        passwordEdit->setEnabled(false);
        passwordEdit->setGeometry(QRect(120, 200, 201, 21));
        passwordEdit->setMaxLength(255);
        tabWidget->addTab(destinationTab, QString());
        createTab = new QWidget();
        createTab->setObjectName(QString::fromUtf8("createTab"));
        progress = new QProgressBar(createTab);
        progress->setObjectName(QString::fromUtf8("progress"));
        progress->setEnabled(false);
        progress->setGeometry(QRect(70, 90, 451, 23));
        progress->setValue(0);
        progress->setAlignment(Qt::AlignCenter);
        progress->setInvertedAppearance(false);
        startButton = new QPushButton(createTab);
        startButton->setObjectName(QString::fromUtf8("startButton"));
        startButton->setGeometry(QRect(250, 140, 95, 31));
        labelCreateGallery = new QLabel(createTab);
        labelCreateGallery->setObjectName(QString::fromUtf8("labelCreateGallery"));
        labelCreateGallery->setGeometry(QRect(10, 40, 561, 21));
        labelCreateGallery->setAlignment(Qt::AlignCenter);
        horizontalLayoutWidget = new QWidget(createTab);
        horizontalLayoutWidget->setObjectName(QString::fromUtf8("horizontalLayoutWidget"));
        horizontalLayoutWidget->setGeometry(QRect(10, 219, 571, 41));
        openBrowserLayout = new QHBoxLayout(horizontalLayoutWidget);
        openBrowserLayout->setSpacing(6);
        openBrowserLayout->setContentsMargins(11, 11, 11, 11);
        openBrowserLayout->setObjectName(QString::fromUtf8("openBrowserLayout"));
        openBrowserLayout->setContentsMargins(0, 0, 0, 0);
        openBrowserLeftSpacer = new QSpacerItem(40, 20, QSizePolicy::Expanding, QSizePolicy::Minimum);

        openBrowserLayout->addItem(openBrowserLeftSpacer);

        openBrowserCheckBox = new QCheckBox(horizontalLayoutWidget);
        openBrowserCheckBox->setObjectName(QString::fromUtf8("openBrowserCheckBox"));
        openBrowserCheckBox->setAutoFillBackground(false);
        openBrowserCheckBox->setChecked(true);

        openBrowserLayout->addWidget(openBrowserCheckBox);

        openBrowserRightSpacer = new QSpacerItem(40, 20, QSizePolicy::Expanding, QSizePolicy::Minimum);

        openBrowserLayout->addItem(openBrowserRightSpacer);

        tabWidget->addTab(createTab, QString());
        aboutTab = new QWidget();
        aboutTab->setObjectName(QString::fromUtf8("aboutTab"));
        aboutTextBrowser = new QTextBrowser(aboutTab);
        aboutTextBrowser->setObjectName(QString::fromUtf8("aboutTextBrowser"));
        aboutTextBrowser->setGeometry(QRect(0, 0, 591, 271));
        aboutTextBrowser->setReadOnly(true);
        aboutTextBrowser->setOpenExternalLinks(true);
        tabWidget->addTab(aboutTab, QString());
        nextButton = new QPushButton(CreateAlbumWizzardClass);
        nextButton->setObjectName(QString::fromUtf8("nextButton"));
        nextButton->setGeometry(QRect(520, 320, 61, 27));
        cancelButton = new QPushButton(CreateAlbumWizzardClass);
        cancelButton->setObjectName(QString::fromUtf8("cancelButton"));
        cancelButton->setGeometry(QRect(260, 340, 81, 27));
        prevButton = new QPushButton(CreateAlbumWizzardClass);
        prevButton->setObjectName(QString::fromUtf8("prevButton"));
        prevButton->setEnabled(false);
        prevButton->setGeometry(QRect(440, 320, 61, 27));

        retranslateUi(CreateAlbumWizzardClass);

        tabWidget->setCurrentIndex(3);
        archiveRes->setCurrentIndex(2);


        QMetaObject::connectSlotsByName(CreateAlbumWizzardClass);
    } // setupUi

    void retranslateUi(QDialog *CreateAlbumWizzardClass)
    {
        CreateAlbumWizzardClass->setWindowTitle(QApplication::translate("CreateAlbumWizzardClass", "Create Web-Slideshow", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        addGallery->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "Adds a gallery page with\n"
"thumbnails of all images", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        addGallery->setText(QApplication::translate("CreateAlbumWizzardClass", "Add Gallery Page", 0, QApplication::UnicodeUTF8));
        titleLable->setText(QApplication::translate("CreateAlbumWizzardClass", "Gallery Title:", 0, QApplication::UnicodeUTF8));
        subTitleLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Gallery Subtitle:", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        titleEdit->setToolTip(QString());
#endif // QT_NO_TOOLTIP
        bottomLineLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Bottom Line(s):", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(infoTab), QApplication::translate("CreateAlbumWizzardClass", "Information", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        archiveCheckBox->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "Adds the a zip-file that contains all images\n"
"for download", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        archiveCheckBox->setText(QApplication::translate("CreateAlbumWizzardClass", "Add Image Archive for Download", 0, QApplication::UnicodeUTF8));
        archiveResLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Image Resolution:", 0, QApplication::UnicodeUTF8));
        archiveRes->clear();
        archiveRes->insertItems(0, QStringList()
         << QApplication::translate("CreateAlbumWizzardClass", "Original Size", 0, QApplication::UnicodeUTF8)
         << QApplication::translate("CreateAlbumWizzardClass", "3000x2250", 0, QApplication::UnicodeUTF8)
         << QApplication::translate("CreateAlbumWizzardClass", "2048x1200", 0, QApplication::UnicodeUTF8)
        );
#ifndef QT_NO_TOOLTIP
        archiveRes->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "Selects the reolution for the images in the download archive", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        navigationLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Slideshow Navigation:", 0, QApplication::UnicodeUTF8));
        controlPanelNavigation->setText(QApplication::translate("CreateAlbumWizzardClass", "Control Panel", 0, QApplication::UnicodeUTF8));
        filmstripNavigation->setText(QApplication::translate("CreateAlbumWizzardClass", "Filmstrip", 0, QApplication::UnicodeUTF8));
        imageCompressionLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Image Compression:", 0, QApplication::UnicodeUTF8));
        compresssionLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "90 %", 0, QApplication::UnicodeUTF8));
        label->setText(QApplication::translate("CreateAlbumWizzardClass", "Image Change:", 0, QApplication::UnicodeUTF8));
        fadeInOut->setText(QApplication::translate("CreateAlbumWizzardClass", "Fade out - Fade in", 0, QApplication::UnicodeUTF8));
        crossFade->setText(QApplication::translate("CreateAlbumWizzardClass", "Cross-Fade", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(configurationTab), QApplication::translate("CreateAlbumWizzardClass", "Configuration", 0, QApplication::UnicodeUTF8));
        protocolLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Protocol:", 0, QApplication::UnicodeUTF8));
        protocolSelector->clear();
        protocolSelector->insertItems(0, QStringList()
         << QApplication::translate("CreateAlbumWizzardClass", "Local File System", 0, QApplication::UnicodeUTF8)
         << QApplication::translate("CreateAlbumWizzardClass", "FTP", 0, QApplication::UnicodeUTF8)
        );
        locationLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Location:", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        locationEdit->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "The directory or url where the web album \n"
"will be created", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        locationEdit->setText(QString());
#ifndef QT_NO_TOOLTIP
        locationButton->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "Choose the destination directory\n"
"for the web album", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        locationButton->setText(QApplication::translate("CreateAlbumWizzardClass", "...", 0, QApplication::UnicodeUTF8));
        portLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Port:", 0, QApplication::UnicodeUTF8));
        usernameLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "User Name:", 0, QApplication::UnicodeUTF8));
        passwordLabel->setText(QApplication::translate("CreateAlbumWizzardClass", "Password:", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(destinationTab), QApplication::translate("CreateAlbumWizzardClass", "Destination", 0, QApplication::UnicodeUTF8));
        progress->setFormat(QApplication::translate("CreateAlbumWizzardClass", "%v / %m", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        startButton->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "Start creating the web album", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        startButton->setText(QApplication::translate("CreateAlbumWizzardClass", "Start", 0, QApplication::UnicodeUTF8));
        labelCreateGallery->setText(QApplication::translate("CreateAlbumWizzardClass", "Press \"Start\" to create album...", 0, QApplication::UnicodeUTF8));
#ifndef QT_NO_TOOLTIP
        openBrowserCheckBox->setToolTip(QApplication::translate("CreateAlbumWizzardClass", "Open the web album in an internet browser\n"
"when finished", 0, QApplication::UnicodeUTF8));
#endif // QT_NO_TOOLTIP
        openBrowserCheckBox->setText(QApplication::translate("CreateAlbumWizzardClass", "Open photo album in web browser when finished", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(createTab), QApplication::translate("CreateAlbumWizzardClass", "Create", 0, QApplication::UnicodeUTF8));
        aboutTextBrowser->setHtml(QApplication::translate("CreateAlbumWizzardClass", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\" \"http://www.w3.org/TR/REC-html40/strict.dtd\">\n"
"<html><head><meta name=\"qrichtext\" content=\"1\" /><style type=\"text/css\">\n"
"p, li { white-space: pre-wrap; }\n"
"</style></head><body style=\" font-family:'Sans Serif'; font-size:9pt; font-weight:400; font-style:normal;\">\n"
"<table border=\"0\" style=\"-qt-table-type: root; margin-top:4px; margin-bottom:4px; margin-left:4px; margin-right:4px;\">\n"
"<tr>\n"
"<td style=\"border: none;\">\n"
"<p align=\"center\" style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"><span style=\" font-weight:600;\">Create Web-Slideshow</span></p>\n"
"<p align=\"center\" style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">Version: </p>\n"
"<p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
""
                        "<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">An open-source program for creating javascript based web-slideshows. Hompepage: <a href=\"http://code.google.com/p/gwtphotoalbum/\"><span style=\" text-decoration: underline; color:#0000ff;\">http://code.google.com/p/gwtphotoalbum/</span></a></p>\n"
"<p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
"<p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">(c) 2008-2012 by Eckhart Arnold (eckhart_arnold@yahoo.de)</p>\n"
"<p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
"<"
                        "p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">Licensed under the Apache License, Version 2.0 (the &quot;License&quot;) </p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">You may obtain a copy of the License at</p>\n"
"<p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"><a href=\"http://www.apache.org/licenses/LICENSE-2.0\"><span style=\" text-decoration: underline; color:#0000ff;\">http://www.apache.org/licenses/LICENSE-2.0</span></a></p>\n"
"<p style=\"-qt-paragraph-type:empty; margin-to"
                        "p:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"></p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">Unless required by applicable law or agreed to in writing, software</p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT</p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the</p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">License for the specific language governing permissions and limitations under</p>\n"
"<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-inden"
                        "t:0; text-indent:0px;\">the License.</p></td></tr></table></body></html>", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(aboutTab), QApplication::translate("CreateAlbumWizzardClass", "About", 0, QApplication::UnicodeUTF8));
        nextButton->setText(QApplication::translate("CreateAlbumWizzardClass", ">>", 0, QApplication::UnicodeUTF8));
        cancelButton->setText(QApplication::translate("CreateAlbumWizzardClass", "Cancel", 0, QApplication::UnicodeUTF8));
        prevButton->setText(QApplication::translate("CreateAlbumWizzardClass", "<<", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class CreateAlbumWizzardClass: public Ui_CreateAlbumWizzardClass {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_CREATEALBUMWIZZARD_H
