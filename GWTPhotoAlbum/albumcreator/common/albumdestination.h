#ifndef ALBUMDESTINATION_H
#define ALBUMDESTINATION_H

#include <QObject>
#include <QList>
#include <QHash>
#include <QMap>
#include <QSize>
#include <QString>

class AlbumDestination : public QObject
{
    Q_OBJECT

public:
    QString		   					infoJSON;
    QList<QString> 					imageNames;
    QList<QSize>   					sizesList;
    QHash<QString, QString> 		captions;
    QHash<QString, QString> 		imageToReskey;
    QMap<QString, QList<QSize> > 	reskeyToSizes;

    virtual ~AlbumDestination();

    bool parseFilenames(QString filenamesJSON);
    bool parseDirectories(QString directoriesJSON);
    bool parseCaptions(QString captionsJSON);
    bool parseReolutions(QString resolutionsJSON);
protected:
    AlbumDestination(QObject *parent);

};

#endif // ALBUMDESTINATION_H
