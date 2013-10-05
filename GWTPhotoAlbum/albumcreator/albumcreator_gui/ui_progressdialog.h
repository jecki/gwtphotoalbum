/********************************************************************************
** Form generated from reading UI file 'progressdialog.ui'
**
** Created: Tue Mar 26 22:24:49 2013
**      by: Qt User Interface Compiler version 4.8.4
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_PROGRESSDIALOG_H
#define UI_PROGRESSDIALOG_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QDialog>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QProgressBar>
#include <QtGui/QPushButton>

QT_BEGIN_NAMESPACE

class Ui_ProgressDialogClass
{
public:
    QLabel *progressLabel;
    QProgressBar *progressBar;
    QPushButton *cancelButton;

    void setupUi(QDialog *ProgressDialogClass)
    {
        if (ProgressDialogClass->objectName().isEmpty())
            ProgressDialogClass->setObjectName(QString::fromUtf8("ProgressDialogClass"));
        ProgressDialogClass->resize(400, 200);
        progressLabel = new QLabel(ProgressDialogClass);
        progressLabel->setObjectName(QString::fromUtf8("progressLabel"));
        progressLabel->setGeometry(QRect(30, 30, 59, 17));
        progressBar = new QProgressBar(ProgressDialogClass);
        progressBar->setObjectName(QString::fromUtf8("progressBar"));
        progressBar->setGeometry(QRect(30, 70, 331, 23));
        progressBar->setValue(24);
        cancelButton = new QPushButton(ProgressDialogClass);
        cancelButton->setObjectName(QString::fromUtf8("cancelButton"));
        cancelButton->setGeometry(QRect(270, 140, 92, 27));

        retranslateUi(ProgressDialogClass);

        QMetaObject::connectSlotsByName(ProgressDialogClass);
    } // setupUi

    void retranslateUi(QDialog *ProgressDialogClass)
    {
        ProgressDialogClass->setWindowTitle(QApplication::translate("ProgressDialogClass", "ProgressDialog", 0, QApplication::UnicodeUTF8));
        progressLabel->setText(QApplication::translate("ProgressDialogClass", "Working...", 0, QApplication::UnicodeUTF8));
        cancelButton->setText(QApplication::translate("ProgressDialogClass", "Cancel", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class ProgressDialogClass: public Ui_ProgressDialogClass {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_PROGRESSDIALOG_H
