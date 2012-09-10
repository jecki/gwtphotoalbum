/*
 * imageitemdelegate.h
 *
 *  Created on: 29.08.2012
 *      Author: eckhart
 */

#ifndef IMAGEITEMDELEGATE_H_
#define IMAGEITEMDELEGATE_H_

#include <qstyleditemdelegate.h>

class ImageItemDelegate: public QStyledItemDelegate {
	Q_OBJECT

public:
	ImageItemDelegate(QObject *parent = 0);
	virtual ~ImageItemDelegate();

	virtual void paint (QPainter *painter, const QStyleOptionViewItem &option,
			const QModelIndex &index) const;

	void highlightDropPosition(const QModelIndex &index);
	void clearDropPosition();

private:
	QModelIndex dropPosition;
};

#endif /* IMAGEITEMDELEGATE_H_ */
