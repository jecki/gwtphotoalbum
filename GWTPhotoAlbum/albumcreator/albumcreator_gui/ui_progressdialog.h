/********************************************************************************
** Form generated from reading UI file 'progressdialog.ui'
**
** Created by: Qt User Interface Compiler version 5.1.1
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_PROGRESSDIALOG_H
#define UI_PROGRESSDIALOG_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QDialog>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QLabel>
#include <QtWidgets/QProgressBar>
#include <QtWidgets/QPushButton>

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
            ProgressDialogClass->setObjectName(QStringLiteral("ProgressDialogClass"));
        ProgressDialogClass->resize(400, 200);
        progressLabel = new QLabel(ProgressDialogClass);
        progressLabel->setObjectName(QStringLiteral("progressLabel"));
        progressLabel->setGeometry(QRect(30, 30, 59, 17));
        progressBar = new QProgressBar(ProgressDialogClass);
        progressBar->setObjectName(QStringLiteral("progressBar"));
        progressBar->setGeometry(QRect(30, 70, 331, 23));
        progressBar->setValue(24);
        cancelButton = new QPushButton(ProgressDialogClass);
        cancelButton->setObjectName(QStringLiteral("cancelButton"));
        cancelButton->setGeometry(QRect(270, 140, 92, 27));

        retranslateUi(ProgressDialogClass);

        QMetaObject::connectSlotsByName(ProgressDialogClass);
    } // setupUi

    void retranslateUi(QDialog *ProgressDialogClass)
    {
        ProgressDialogClass->setWindowTitle(QApplication::translate("ProgressDialogClass", "ProgressDialog", 0));
        progressLabel->setText(QApplication::translate("ProgressDialogClass", "Working...", 0));
        cancelButton->setText(QApplication::translate("ProgressDialogClass", "Cancel", 0));
    } // retranslateUi

};

namespace Ui {
    class ProgressDialogClass: public Ui_ProgressDialogClass {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_PROGRESSDIALOG_H
