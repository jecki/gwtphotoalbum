/*
 * Copyright 2011 Eckhart Arnold (eckhart_arnold@hotmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


#include "albumcreator.h"
#include "ui_albumcreator.h"

#include <QtGui>
#include <QMessageBox>
#include <QImageReader>
#include <QMap>
#include <QVariant>
#include <QByteArray>
#include <QModelIndex>
#include <QDir>
#include <QFileIconProvider>
#include <QFileSystemModel>
#include <QVariant>
#include <QIcon>
#include <QFileInfo>
#include <QPixmap>

#include "filterproxy_class.h"
#include "toolbox.h"


QList<QString> AlbumCreator::FilterProxy::imageFormats = getSupportedImageFormats();

IconCache AlbumCreator::iconCache;


class AlbumCreator::IconProvider : public QFileIconProvider {
public:
	IconProvider(QListView *fileBrowser)
		: placeholder(IconCache::sizeGuard(QPixmap(":/:/images/image_placeholder.png"),
			fileBrowser->iconSize())),
		  folderIcon(IconCache::sizeGuard(
				  (QFileIconProvider::icon(QFileIconProvider::Folder)).pixmap(
						  fileBrowser->iconSize()), fileBrowser->iconSize() ))
	{
		this->fileBrowser = fileBrowser;
	}

	virtual ~IconProvider() { }

	virtual QIcon icon(const QFileInfo &info) const
	{
		if (!info.isDir()) {
			QIcon icon;
			QSize iconSize = fileBrowser->iconSize();
			if (FilterProxy::hasSupportedFormat(info.filePath()) &&
				iconCache.get(info.filePath(), icon, iconSize) ) {
				return icon;
			} else {
				// return QFileIconProvider::icon(QFileIconProvider::File);
				return placeholder;
			}
		} else return folderIcon;
	}

private:
	QListView *fileBrowser;
	QIcon	  placeholder;
	QIcon	  folderIcon;
};



AlbumCreator::AlbumCreator(QWidget *parent) :
        QMainWindow(parent),
        ui(new Ui::AlbumCreator)
{
    ui->setupUi(this);


    // Hack: Adjust sizes a little, so that 2 images are displayed in a row
    // Seems, I need to rewrite the show() method...
//	QList<int> split = ui->mainSplitter->sizes();
//	std::cout << split[0] << ", " << split[1] << std::endl;
//	split[0] -= 20;  split[1] += 20;
//	ui->mainSplitter->setSizes(split);

    unsavedData = false;

    QList<int> destPaneSizeRatios;
    destPaneSizeRatios << 300 << 0;
    ui->destImagePane->setSizes(destPaneSizeRatios);
    captionCollapsedState = ui->destImagePane->saveState();
    ui->destImagePane->setCollapsible(0, false);

    QList<int> mainSizeRatios;
    mainSizeRatios << 200 << 400;
    ui->mainSplitter->setSizes(mainSizeRatios);
    ui->mainSplitter->setCollapsible(1, false);

    directory.setFilter(QDir::AllEntries|QDir::AllEntries);
    directory.setSupportedDragActions(Qt::CopyAction);
    currentDir = QDir::home();
    directory.setRootPath(QDir::rootPath());

    QObject::connect(&iconCache, SIGNAL(iconReady(const QString, const QIcon)),
    		         this, SLOT(iconReady(const QString, const QIcon)));
    QObject::connect(&iconCache, SIGNAL(finishedLoading()),
    		         this, SLOT(allIconsReady()));

    iconProvider = new IconProvider(ui->fileBrowser);
    directory.setIconProvider(iconProvider);

    dirProxy = new FilterProxy(&directory);
    ui->fileBrowser->setModel(dirProxy);

    QString path = currentDir.path();
    iconCache.dirChanged(path);
    ui->pathField->setText(path);
    ui->fileBrowser->setRootIndex(dirProxy->indexOfPath(path));

    QObject::connect(this, SIGNAL(dirChanged(const QString &)),
					 ui->pathField, SLOT(setText(const QString &)) );
    QObject::connect(this, SIGNAL(dirChanged(const QString &)),
    		         &iconCache, SLOT(dirChanged(const QString &)) );

    collection = new ImageCollection();
    model = new ImageListModel(collection->imageList);
    ui->albumView->setModel(model);

    Q_ASSERT_X(model != NULL && collection != NULL && iconProvider != NULL &&
    		   dirProxy != NULL && ui != NULL,
    		   "AlbumCreator::AlbumCreator()", "out of heap space!?");
}


AlbumCreator::~AlbumCreator()
{
	delete model;
	delete collection;
	delete iconProvider;
    delete dirProxy;
    delete ui;
}


void AlbumCreator::changeEvent(QEvent *e)
{
    QMainWindow::changeEvent(e);
    switch (e->type()) {
    case QEvent::LanguageChange:
        ui->retranslateUi(this);
        break;
    default:
        break;
    }
}

void AlbumCreator::closeEvent(QCloseEvent *event) {
	if (!unsavedData || QMessageBox::question(this, tr("Really Exit?"),
		tr("Do you really want to exit?\nAllchanges will be lost!"),
		QMessageBox::Yes|QMessageBox::No|QMessageBox::Cancel) == QMessageBox::Yes) {
		event->accept();
    } else {
    	event->ignore();
    }
}

void AlbumCreator::internal_dirChanged()
{
	QString path = currentDir.path();
	QModelIndex index = dirProxy->indexOfPath(path);
    ui->fileBrowser->setRootIndex(index);
	emit dirChanged(path);

	// needed to update view correctly under all circumstances, but why?
	dirProxy->dataUpdated(index, index);
	directory.setIconProvider(iconProvider); // find some better solution here!!!
	// cout << path.toStdString() << " " << dirProxy->rowCount(dirProxy->indexOfPath(currentDir.path())) << endl;
}


void AlbumCreator::on_actionAbout_triggered()
{
    QMessageBox::information(this, tr("About AlbumCreator"),
                     tr("AlbumCreator Version 0.1\n\n"\
                        "(c) 2009 GPL\n\n by Eckhart Arnold"));
}

void AlbumCreator::on_actionExit_triggered()
{
	close();
}

void AlbumCreator::on_fileBrowser_doubleClicked(const QModelIndex &index)
{
	QString name = dirProxy->fileName(index);
	if (dirProxy->isDir(index)) {
		if ( (name == ".." && currentDir.cdUp()) ||
			 (name != "."  && currentDir.cd(name)) ) {
			internal_dirChanged();
		}
	} else {

	}
}

void AlbumCreator::on_parentDirButton_clicked()
{
	if (currentDir.cdUp()) {
		internal_dirChanged();
	}
}

void AlbumCreator::on_pathField_textChanged(const QString &path)
{
	if (currentDir.cd(path)) {
	    ui->fileBrowser->setRootIndex(dirProxy->indexOfPath(path));
	}
}


void AlbumCreator::iconReady(const QString filePath, const QIcon icon) {
	QModelIndex index = dirProxy->indexOfPath(filePath);
	// cout << dirProxy->parent(index) << endl;
	dirProxy->setData(index, icon, QFileSystemModel::FileIconRole);
	dirProxy->dataUpdated(index, index);
	// unfortunately this is needed, because the file icon is for some reason not set by dirProxy->setData :(
	// maybe this should not be called every time to avoid slowdowns?
	directory.setIconProvider(iconProvider);
}

void AlbumCreator::allIconsReady() {
	// This works, but I do not really know why...
	// find some better solution here!!!
	QModelIndex index = dirProxy->indexOfPath(currentDir.path());
	dirProxy->dataUpdated(index, index);
	directory.setIconProvider(iconProvider);
}

