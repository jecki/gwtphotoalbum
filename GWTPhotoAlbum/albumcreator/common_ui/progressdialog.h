#ifndef PROGRESSDIALOG_H
#define PROGRESSDIALOG_H

#include <QtGui/QDialog>
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
