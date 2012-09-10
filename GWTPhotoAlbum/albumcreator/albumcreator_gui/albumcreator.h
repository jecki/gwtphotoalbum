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


#ifndef ALBUMCREATOR_H
#define ALBUMCREATOR_H

#include <QMainWindow>
#include <QFileSystemModel>
#include <QSortFilterProxyModel>
#include <QDir>
#include <QList>
#include <QByteArray>

#include "iconcache.h"
#include "imagelistmodel.h"
#include "../common/imageitem.h"
#include "../common/imagecollection.h"

namespace Ui {
    class AlbumCreator;
}


class AlbumCreator : public QMainWindow {
    Q_OBJECT

public:
    class FilterProxy;

    AlbumCreator(QWidget *parent = 0);
    virtual ~AlbumCreator();

protected:
    void changeEvent(QEvent *e);
    virtual void closeEvent(QCloseEvent *event);

private:
    class IconProvider;
    // class GeomtricCounter;
    static IconCache iconCache;

    QDir 			 currentDir;
    QFileSystemModel directory;
    FilterProxy      *dirProxy;
    IconProvider     *iconProvider;
    bool			 unsavedData;
    QByteArray       captionCollapsedState, captionExpandedState;
    //GeometricCounter counter;

    ImageCollection	 *collection;
    ImageListModel   *model;

    Ui::AlbumCreator *ui;

    void internal_dirChanged();

public Q_SLOTS:
    void on_actionAbout_triggered();
    void on_actionExit_triggered();

    void on_fileBrowser_doubleClicked(const QModelIndex &index);
    void on_parentDirButton_clicked();
    void on_pathField_textChanged(const QString &path);

Q_SIGNALS:
  	void dirChanged(const QString &);

private Q_SLOTS:
    void iconReady(const QString filePath, const QIcon icon);
	void allIconsReady();
};

#endif // ALBUMCREATOR_H
