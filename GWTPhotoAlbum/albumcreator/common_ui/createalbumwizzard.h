#ifndef CREATEALBUMWIZZARD_H
#define CREATEALBUMWIZZARD_H

#include <Qt>
#if QT_VERSION >= 0x050000
#include <QtWidgets/QDialog>
#else
#include <QtGui/QDialog>
#endif
#include "ui_createalbumwizzard.h"
#include "imagecollection.h"

namespace Ui {
    class CreateAlbumWizzard;
}

class CreateAlbumWizzard : public QDialog
{
    Q_OBJECT

public:
    Q_PROPERTY(bool changesLostDialog READ hasChangesLostDialog WRITE setChangesLostDialog)
    Q_PROPERTY(bool aboutTab READ hasAboutTab WRITE setAboutTab)

    CreateAlbumWizzard(ImageCollection &ic, QWidget *parent = 0);
    ~CreateAlbumWizzard();

    QString albumUrl() const;
    bool hasChangesLostDialog() { return m_changesLostDialog; }
    bool hasAboutTab() { return m_aboutTab; }

protected:
    void importData(const ImageCollection &ic);
    void exportData(ImageCollection &ic) const;

    bool queryStopAlbumCreation();

private:
    Ui::CreateAlbumWizzardClass ui;
    ImageCollection &ic;
    bool m_changesLostDialog;
    bool m_aboutTab;

    QFuture<bool> 	albumTask;
    QString			destPath;
    QString			userLinkText;

    QString albumNameSuggestion(const QString &path) const;
    QString albumPath(const QString &path) const;
    QString albumParentDir(const QString &path) const;

    QString archiveNameSuggestion() const;
    int archiveLinkPosition(const QString &html) const;
    QString archiveLinkText(const QString &html) const;
    QString &removeArchiveLink(QString &html) const;
    QString &addArchiveLink(QString &html, int position=-1, QString linkText="") const;

public Q_SLOTS:
	void on_tabWidget_currentChanged(int index);
	void on_nextButton_clicked();
	void on_prevButton_clicked();
	void on_compressionSlider_valueChanged(int value);
	void on_locationButton_clicked();
	void on_locationEdit_editingFinished();
	void on_startButton_clicked();
	void on_cancelButton_clicked();
	void on_archiveCheckBox_stateChanged(int state);
	void on_titleEdit_editingFinished();

	void progress(ImageCollection *source, int nr);  // progress callback
	void finished(bool completed, const QString &errorMsg);

	void setChangesLostDialog(bool value) { m_changesLostDialog = value; }
	void setAboutTab(bool value);
};

#endif // CREATEALBUMWIZZARD_H
