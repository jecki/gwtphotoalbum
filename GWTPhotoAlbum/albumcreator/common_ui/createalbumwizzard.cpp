
#include "createalbumwizzard.h"
#include "imagecollection.h"
#include "toolbox.h"

#include <Qt>
#include <QMessageBox>
#include <QFileDialog>
#include <QUrl>
#include <QDesktopServices>
#if QT_VERSION >= 0x050000
#include <QtConcurrent/QtConcurrentRun>
#else
#include <QtCore/QtConcurrentRun>
#endif

#ifndef NDEBUG
#include <iostream>
using namespace std;
#endif


/*!
 * Constructor for class CreateAlbumWizzard. Builds up the dialog's ui and
 * makes the necessary connections for signals and slots.
 *
 * @param ic      reference to an image collection that must contain at least
 *                one image.
 * @param parent  a pointer to the parent widget
 */
CreateAlbumWizzard::CreateAlbumWizzard(ImageCollection &ic, QWidget *parent)
    : QDialog(parent), ic(ic), m_changesLostDialog(true), m_aboutTab(true)
{
	ui.setupUi(this);
	Q_ASSERT_X(ic.imageList.length() > 0, "CreateAlbumWizzard constructor",
			   "image collection is empty.");

	while (ui.archiveRes->count() > 0) {
		ui.archiveRes->removeItem(0);
	}

	foreach(const QSize &size, ImageCollection::Archive_Sizes) {
		if (size == ImageCollection::Original_Size) {
			ui.archiveRes->addItem(tr("Original Size"), size);
		} else {
			ui.archiveRes->addItem(QString::number(size.width())+"x"+QString::number(size.height()),
								   size);
		}
	}

	while (!ui.protocolSelector->itemText(1).isEmpty()) {
		ui.protocolSelector->removeItem(1); // TODO: implement FTP
	}

	connect(&ic, SIGNAL(progress(ImageCollection *, int)),
			this, SLOT(progress(ImageCollection *, int)));
	connect(&ic, SIGNAL(finished(bool, const QString &)),
			this, SLOT(finished(bool, const QString &)));

	importData(ic);
	// std::cout << ic.imageList.length() << std::endl;
	ui.progress->setRange(0, ic.imageList.length());
	ui.progress->setValue(0);
	ui.tabWidget->setCurrentIndex(0);
	ui.locationEdit->setText(albumPath(QDir::homePath()));

	// insert version number in about text
	ui.aboutTextBrowser->moveCursor(QTextCursor::Start);
	ui.aboutTextBrowser->moveCursor(QTextCursor::Down);
	ui.aboutTextBrowser->moveCursor(QTextCursor::EndOfLine);
	ui.aboutTextBrowser->insertPlainText(versionNumber());
	ui.aboutTextBrowser->moveCursor(QTextCursor::Start);

	setAboutTab(false);
}

/*!
 * Destructor of class CreateAlbumWizzard.
 */
CreateAlbumWizzard::~CreateAlbumWizzard()
{

}


/*!
 * Returns the Url of the album after its creation or an empty string if no
 * album was created.
 *
 * @return  Url of the created photo album.
 */
QString CreateAlbumWizzard::albumUrl() const
{
	if (albumTask.isFinished() && albumTask.result()) {
		return destPath + "/index.html";
	} else {
		return QString();
	}
}



/*!
 * Imports all data (except that concerning the destination for the album) from
 * an ImageCollection object.
 *
 * @param ic  the image collection object from which the data for setting the
 *            dialog's widgets is to be taken.
 */
void CreateAlbumWizzard::importData(const ImageCollection &ic)
{
	ui.titleEdit->setText(ic.title);
	ui.subTitleEdit->setText(ic.subtitle);
	ui.bottomLineEdit->setPlainText(ic.bottom_line);
	ui.addGallery->setChecked(ic.presentation_type == "gallery");

	ui.archiveCheckBox->setChecked(!ic.archiveName.isEmpty());
	ui.archiveRes->setCurrentIndex(bestMatch(ic.archiveSize, ic.Archive_Sizes));

	ui.filmstripNavigation->setChecked(ic.layout_data.contains("F"));
	ui.crossFade->setChecked(ic.image_fading > 0);

	Q_ASSERT_X(ic.compression >= ui.compressionSlider->minimum() &&
			   ic.compression <= ui.compressionSlider->maximum(),
			"CreateAlbumWizzard::importData",
			"compression value out of bounds");
	ui.compressionSlider->setValue(ic.compression);


}


/*!
 * Exports the data the user has entered in the dialog to an ImageCollection
 * object.
 *
 * @param ic  the image collection object to which the dialog's data is
 * 			  exported
 */
void CreateAlbumWizzard::exportData(ImageCollection &ic) const
{
	ic.title = ui.titleEdit->text();
	ic.subtitle = ui.subTitleEdit->text();
	ic.bottom_line = ui.bottomLineEdit->toPlainText();
	ic.bottom_line.replace("\n", "<br />");
	if (ui.addGallery->isChecked()) {
		ic.presentation_type = "gallery";
	} else {
		ic.presentation_type = "slideshow";
	}
	if (ui.archiveCheckBox->isChecked()) {
		ic.archiveName = archiveNameSuggestion();
	} else {
		ic.archiveName = "";
	}
	ic.archiveSize = ui.archiveRes->itemData(ui.archiveRes->currentIndex()).toSize();
	if (ui.filmstripNavigation->isChecked()) {
		ic.layout_data = "IOF";
	} else {
		ic.layout_data = "IOP";
	}
	if (ui.crossFade->isChecked()) {
		ic.image_fading = 1000;
	} else {
		ic.image_fading = -700;
	}
	ic.compression = ui.compressionSlider->value();
}


/*!
 * Displays a message box that asks the user, if album creation shall be stopped.
 * Stops album creation and returns true, if the user clicks Yes. Returns
 * false if the user clicks No, Cancel or if album creation was not running or
 * has already finished.
 * @return true, if album creation has been stopped on behalf of the user,
 *         false otherwise.
 */
bool CreateAlbumWizzard::queryStopAlbumCreation()
{
	QMessageBox query;
	query.setText(tr("Do you really want to stop the album creation?"));
	query.setInformativeText(tr("All progress will be lost."));
	query.setStandardButtons(QMessageBox::Yes|QMessageBox::No|QMessageBox::Cancel);
	query.setDefaultButton(QMessageBox::Yes);
	if (query.exec() == QMessageBox::Yes) {
		if (ic.isRunning()) {
			ui.labelCreateGallery->setText(tr("Album creation interrupted!"));
			ic.stopAlbumCreation();
			return true;
		}
	}
	return false;
}


/*!
 * Returns a suggestion for an album directory name. This is either the
 * last part of the path in 'locationEdit' or it is derived from the gallery
 * name.
 *
 * @parameter path  the output path for the photo album
 * @return a suggested album directory name.
 */
QString CreateAlbumWizzard::albumNameSuggestion(const QString &path) const
{
	QFileInfo location(path);
	if (location.exists()) {
		QString name = deriveFileName(ui.titleEdit->text());
		if (name.isEmpty()) {
			return "album";
		} else {
			return name;
		}
	} else {
		location.setFile(ui.locationEdit->text());
		return location.fileName();
	}
}


/*!
 *	Generates a valid possible path name for an album destination path from
 *	a given path name. To be valid the path name must not represent any
 *	existing file or directory. If the given path name is not valid then a
 *	valid path name is generated by appending a name generated from the
 *	gallery title or a generic name.
 *
 *	@parameter path	 a path that is to be checked for validity and used
 *	                 as basis for generating a valid path name.
 *	@return  a valid path name for the generation of the album.
 */
QString CreateAlbumWizzard::albumPath(const QString &path) const
{
	QFileInfo	location(path);
	QString		locationStr;
	int 		i = 2;

	if (location.exists()) {
		if (!location.isDir()) {
			location.setFile(location.path());
		}
		QString fp(location.filePath());
		if (!fp.endsWith('/'))  fp += '/';
		location.setFile(fp + albumNameSuggestion(path));
		locationStr = location.filePath();
		while (location.exists()) {
			location.setFile(locationStr + QString::number(i, 10));
			i++;
		}
	}
	return location.filePath();
}


/*!
 * Returns the path to the first valid parent directory of an album path.
 *
 * @parameter  a path to an album (does not need to exist)
 * @return     the first existing parent directory to the album path-
 */
QString CreateAlbumWizzard::albumParentDir(const QString &path) const
{
	QFileInfo location(path);
	while (!(location.isDir() && location.exists())) {
		location.setFile(location.path());
	}
	return location.filePath();
}


/*!
 * Returns a suggested name for a zip-archive of all images. The name is
 * derived from the gallery title.
 *
 * @return a suggested name for the archive
 */
QString CreateAlbumWizzard::archiveNameSuggestion() const
{
	QString name = deriveFileName(ui.titleEdit->text());
	if (name.isEmpty()) {
		name = "all_pictures.zip";
	}
	return name+".zip";
}


static const QString ARCHIVE_LINK_START("\n<a name=\"archive link\"");
static const QString ARCHIVE_LINK_END("</a>");
static const QString GENERIC_ARCHIVE_LINK_TEXT(QObject::tr("download all pictures"));

/*!
 * Returns the position of the link to the image archive in the given html
 * chunk. If there is no link, -1 will be returned.
 *
 * @parameter html  an html chunk which will be searched for a link to an
 *                  archive.
 * @return the position of the archive link or -1 if it does not exist.
 */
int CreateAlbumWizzard::archiveLinkPosition(const QString &html) const
{
	return html.indexOf(ARCHIVE_LINK_START);
}


/*!
 * Returns the archive link text from an html or an empty string if the chunk
 * does not contain an archive link.
 *
 * @parameter html  an html chunk
 * @return the archive link text
 */
QString CreateAlbumWizzard::archiveLinkText(const QString &html) const
{
	int i = archiveLinkPosition(html);
	if (i < 0) {
		return GENERIC_ARCHIVE_LINK_TEXT;
	} else {
		int k = html.indexOf(">", i + ARCHIVE_LINK_START.length()) + 1;
		int l = html.indexOf("<", k) - k;
		return html.mid(k, l);
	}
}


/*!
 * Returns an html chunk from which the (first) archive link has been
 * stripped.
 *
 * @parameter html  the html chunk containing an archive link.
 * @return  the html chunk without archive link.
 */
QString &CreateAlbumWizzard::removeArchiveLink(QString &html) const
{
	int i = archiveLinkPosition(html);
	if (i >= 0) {
		int l = html.indexOf(ARCHIVE_LINK_END, i)-i +ARCHIVE_LINK_END.length();
		html.remove(i, l);
	}
	return html;
}


/*!
 * Returns a html chunk to which an archive link as been added at a
 * specific position.
 *
 * @parameter html      the html chunk to which the link is to be added.
 * @parameter position  the position, where the link is to be inserted or
 *                      -1 if it is to be added at the end.
 * @parameter linkText  the text for the link. If empty, the standard text
 *                      "download all pictures" will be added.
 * @return  the html chunk to which the link has been added.
 */
QString &CreateAlbumWizzard::addArchiveLink(QString &html, int position, QString linkText) const
{
	if (linkText.isEmpty()) {
		linkText = GENERIC_ARCHIVE_LINK_TEXT;
	}
	QString link(ARCHIVE_LINK_START+" style=\"text-align:center;\" href=\""
	             +archiveNameSuggestion()+"\">"+linkText+ARCHIVE_LINK_END);
	if (position < 0) {
		html.append(link);
	} else {
		html.insert(position, link);
	}
	return html;
}



/*!
 * Slot that receives tabChanged signals from the dialog's tabWidget.
 * Changes must be reflected in the the enabled and disabled status of
 * the next and previous buttons of the dialog.
 *
 * @param  index  the index of the current tab.
 */
void CreateAlbumWizzard::on_tabWidget_currentChanged(int index)
{
	if (index < 0 || index >= ui.tabWidget->count()) {
		ui.nextButton->setEnabled(false);
		ui.prevButton->setEnabled(false);
	} else if (index == ui.tabWidget->count()-1) {
		ui.nextButton->setEnabled(false);
		ui.prevButton->setEnabled(true);
	} else if (index == 0) {
		ui.nextButton->setEnabled(true);
		ui.prevButton->setEnabled(false);
	} else {
		ui.nextButton->setEnabled(true);
		ui.prevButton->setEnabled(true);
	}
}

/*!
 * Slot that responds to a click to the next button by switching
 * to the next tab of the dialog's tab widget.
 */
void CreateAlbumWizzard::on_nextButton_clicked()
{
	int newIndex = ui.tabWidget->currentIndex()+1;
	if (newIndex < ui.tabWidget->count()) {
		ui.tabWidget->setCurrentIndex(newIndex);
	}
}

/*!
 * Slot that responds to a click to the previous button by switching
 * to the previous tab of the dialog's tab widget.
 */
void CreateAlbumWizzard::on_prevButton_clicked()
{
	int newIndex = ui.tabWidget->currentIndex()-1;
	if (newIndex >= 0) {
		ui.tabWidget->setCurrentIndex(newIndex);
	}
}

/*!
 * Slot that reacts to movements of the compression slider by
 * adjusting the compression label accordingly.
 */
void CreateAlbumWizzard::on_compressionSlider_valueChanged(int value)
{
	ui.compresssionLabel->setText(QString::number(value)+" %");
}

/*!
 * Slot reacts to a click on the location button by opening a file dialog for
 * selecting a directory. As the album must be stored in a new directory, the
 * name is adjusted if it represents a directory that already exists.
 */
void CreateAlbumWizzard::on_locationButton_clicked()
{
	QFileDialog fd;
	QString		location(albumParentDir(ui.locationEdit->text()));

	fd.setWindowTitle(tr("Select a location for the album..."));
	fd.setDirectory(location);
	fd.setAcceptMode(QFileDialog::AcceptOpen);
	fd.setFileMode(QFileDialog::Directory);
	fd.setOptions(QFileDialog::ShowDirsOnly);
	// fd.setFilter(QDir::Dirs|QDir::Drives);
	fd.setWindowTitle(tr("Pick image directory or files..."));
	if (fd.exec()) {
		QStringList paths = fd.selectedFiles();
		// std::cout << paths[0].toStdString() << std::endl;
		ui.locationEdit->setText(albumPath(paths[0]));
	}
}

/*!
 * Slot that reacts to a click on the start button and either starts the album
 * creation or stops it in case it is already running. Changes the test on the
 * button from start to stop (or stop to start) accordingly.
 */
void CreateAlbumWizzard::on_startButton_clicked()
{
	if (ic.isRunning()) {
		if (queryStopAlbumCreation()) {
			ic.stopAlbumCreation();
		}
	} else {
		if (ic.imageList.length() == 0) return;  // just for safety !
		destPath = ui.locationEdit->text();
		if (destPath.isEmpty()) {
			QMessageBox	info;
			info.setText(tr("No destination for album selected!"));
			info.setInformativeText(tr("Switching now to the \"Destination\" tab."));
			info.setStandardButtons(QMessageBox::Ok);
			info.setDefaultButton(QMessageBox::Ok);
			info.exec();
			ui.tabWidget->setCurrentIndex(ui.tabWidget->currentIndex()-1);
			return;
		}
		ui.startButton->setText("Stop");
		ui.labelCreateGallery->setText(tr("Creating album:  ") + QFileInfo(destPath).baseName()); // + album name
		exportData(ic);
		albumTask = QtConcurrent::run(&ic, &ImageCollection::createAlbum, destPath);
	}
}

/*!
 * Slot that reacts to a click on the cancel button. Cancels album creation
 * (after a saftey query) and leaves the dialog.
 */
void CreateAlbumWizzard::on_cancelButton_clicked()
{
	if (ic.isRunning()) {
		if (queryStopAlbumCreation()) {
			albumTask.waitForFinished();
			reject();
		}
	} else if (m_changesLostDialog ){
		QMessageBox	query;
		query.setText(tr("Do you really want to leave album creation?"));
		query.setInformativeText(tr("All settings may be lost."));
		query.setStandardButtons(QMessageBox::Yes|QMessageBox::No|QMessageBox::Cancel);
		query.setDefaultButton(QMessageBox::Yes);
		if (query.exec() == QMessageBox::Yes) {
			reject();
		}
	} else {
		reject();
	}
}



/*!
 * Slot that reacts to a change of state of the archive check box. Adds or
 * removes a link to the image archive to the bottom of the gallery
 * page accordingly. The link can be edited by the user by going back
 * to information tab.
 *
 * @parameter int  the state of the archive check box.
 */
void CreateAlbumWizzard::on_archiveCheckBox_stateChanged(int state)
{
	QString html = ui.bottomLineEdit->toPlainText();
	if (state == Qt::Checked) {
		if (userLinkText.isEmpty()) {
			userLinkText = archiveLinkText(html);
		}
		int pos = archiveLinkPosition(html);
		removeArchiveLink(html);
		addArchiveLink(html, pos, userLinkText);
	} else {
		if (archiveLinkPosition(html) >= 0) {
			userLinkText = archiveLinkText(html);
		}
		removeArchiveLink(html);
	}
	ui.bottomLineEdit->setPlainText(html);
}


/*!
 * Slot that reacts to the state of the gallery title. Adjusts the name
 * of the image archive if the archive check box is selected.
 *
 * @parameter text  the new text of the title edit widget.
 */
void CreateAlbumWizzard::on_titleEdit_editingFinished()
{
	if (ui.archiveCheckBox->checkState() == Qt::Checked) {
		on_archiveCheckBox_stateChanged(Qt::Checked);
	}
}



/*!
 * Slots that takes notice of the progress of an ongoing album creation
 * process and adjusts the progress bar accordingly.
 *
 * @parameter source  the image collection object that sent the signal
 * @parameter nr      the current progress
 */
void CreateAlbumWizzard::progress(ImageCollection *source, int nr)
{
	(void) source;
	ui.progress->setValue(nr);
}


/*!
 * Slot that is called when album creation is finished. Display an
 * error or warning message box in case an error or any warnings dir
 * occur. Rests the start button text to "Start".
 *
 * @parameter completed  true, if album creation could be completed
 * @parameter errorMsg   any error messages or warnings.
 */
void CreateAlbumWizzard::finished(bool completed, const QString &errorMsg)
{
	QMessageBox	info;

	if (!completed) {
		if (!errorMsg.isEmpty()) {
			info.setText(tr("Error while creating album:"));
			info.setInformativeText(errorMsg);
			info.setStandardButtons(QMessageBox::Ok);
			info.setDefaultButton(QMessageBox::Ok);
			info.exec();
		}
		ic.rollback();
	} else if (!errorMsg.isEmpty()) {
		info.setText(tr("Warnings while creating album:"));
		info.setInformativeText(errorMsg);
		info.setStandardButtons(QMessageBox::Ok);
		info.setDefaultButton(QMessageBox::Ok);
		info.exec();
	} else if (ui.openBrowserCheckBox->isChecked()) {
	    QString url = albumUrl();
	    if (!url.isEmpty()) {
		    std::cout << url.toStdString() << std::endl;
	    	QDesktopServices::openUrl(QUrl(url));
	    	std::cout << "ready." << std::endl;
	    }
	}
	ui.progress->setValue(0);
	ui.startButton->setText(tr("Start"));
	ui.labelCreateGallery->setText(tr("Press \"Start\" to create album..."));
	accept();
}



/*!
 * Slot that turns the about tab on or off. The about tab can only be turned
 * on or off when the dialog is not visible!
 *
 * @parameter value  true, if the about tab is visible in the dialog.
 */
void CreateAlbumWizzard::setAboutTab(bool value) {
	int numTabs = ui.tabWidget->count();

	Q_ASSERT_X((m_aboutTab && numTabs == 5) || (!m_aboutTab && numTabs == 4),
			   "CreateAlbumWizzard::setAboutTab",
			   "actual number of tabs does not match expected number of tabs!");

	if (!isVisible()) {
		if (m_aboutTab && !value) {
			m_aboutTab = false;
			ui.tabWidget->removeTab(numTabs-1);
		} else if (!m_aboutTab && value) {
			m_aboutTab = true;
			ui.tabWidget->insertTab(numTabs, ui.aboutTab, tr("About"));
		}
	}
}

