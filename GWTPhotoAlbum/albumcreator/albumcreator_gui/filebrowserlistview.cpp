/*
 * Copyright 2012 Eckhart Arnold (eckhart_arnold@hotmail.com).
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

#include "filebrowserlistview.h"
#include "albumcreator.h"
#include "toolbox.h"

#ifndef NDEBUG
#include <QDebug>
#endif

FileBrowserListView::FileBrowserListView(QWidget *parent)
	: QListView(parent)
{

}

FileBrowserListView::~FileBrowserListView()
{

}


void FileBrowserListView::startDrag(Qt::DropActions supportedActions) {
	QAbstractItemModel  *itemModel = model();
	QItemSelectionModel *selection = selectionModel();
	QModelIndexList		selected = selection->selectedIndexes();
	QModelIndex			current = selection->currentIndex();

	if (!selected.contains(current)) {
		selected.append(current);
	}

	QModelIndexList copy = selected;
	foreach(QModelIndex index, copy) {
		QString path = qvariant_cast<QString>(
				itemModel->data(index, Qt::DisplayRole));
		if (path == ".." or path == ".") {
			selected.removeOne(index);
		}
	}

	QMimeData *mime = itemModel->mimeData(selected);
	if (!mime)
		return;

//	QString path = qvariant_cast<QString>(
//			itemModel->data(current, QFileSystemModel::FileNameRole));
	QVariant decoration = itemModel->data(current, Qt::DecorationRole);
	Q_ASSERT(decoration.type() == QVariant::Icon);
	QPixmap pixmap = IconCache::sizeGuard((qvariant_cast<QIcon>(decoration))
			.pixmap(iconSize()), iconSize());

	QByteArray	imageData;
	QDataStream	stream(&imageData, QIODevice::WriteOnly);
	stream << pixmap;
	mime->setData("image/x-pixmap", imageData);

	QDrag *drag = new QDrag(this);
	drag->setPixmap(pixmap);
	drag->setMimeData(mime);
	drag->setHotSpot(QPoint(pixmap.width()/2, pixmap.height()/2));

	drag->exec(supportedActions, Qt::CopyAction);
}


//void FileBrowserListView::dragEnterEvent(QDragEnterEvent *event) {
//    if (event->mimeData()->hasFormat("image/x-pixmap"))
//        event->accept();
//    else
//        event->ignore();
//}


void FileBrowserListView::dragMoveEvent(QDragMoveEvent *event) {
    if (event->mimeData()->hasFormat("image/x-pixmap")) {
        event->setDropAction(Qt::CopyAction);
        event->accept();
    } else
        event->ignore();
}


void FileBrowserListView::dropEvent(QDropEvent *event) {
	event->setDropAction(Qt::IgnoreAction);
	event->ignore();
}

