#ifndef PROGRESSDIALOG_H
#define PROGRESSDIALOG_H

#include <Qt>
#if QT_VERSION >= 0x050000
#include <QtWidgets/QDialog>
#else
#include <QtGui/QDialog>
#endif
#include "ui_progressdialog.h"

class ProgressDialog : public QDialog
{
    Q_OBJECT

public:
    ProgressDialog(QWidget *parent = 0);
    ~ProgressDialog();

private:
    Ui::ProgressDialogClass ui;
};

#endif // PROGRESSDIALOG_H
