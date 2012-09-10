#include "albumdestination.h"



AlbumDestination::~AlbumDestination()
{

}


AlbumDestination::AlbumDestination(QObject *parent)
	: QObject(parent)
{

}


bool AlbumDestination::parseFilenames(QString filenamesJSON)
{
	(void) filenamesJSON;

	bool	ok = true;

	return ok;
}


bool AlbumDestination::parseDirectories(QString directoriesJSON)
{
	(void) directoriesJSON;

	bool	ok = true;

	return ok;
}

bool AlbumDestination::parseCaptions(QString captionsJSON)
{
	(void) captionsJSON;

	bool	ok = true;

	return ok;
}


bool AlbumDestination::parseReolutions(QString resolutionsJSON)
{
	(void) resolutionsJSON;

	bool	ok = true;

	return ok;
}

