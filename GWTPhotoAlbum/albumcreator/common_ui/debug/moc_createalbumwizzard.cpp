/****************************************************************************
** Meta object code from reading C++ file 'createalbumwizzard.h'
**
** Created: Wed Mar 6 23:03:14 2013
**      by: The Qt Meta Object Compiler version 63 (Qt 4.8.4)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../createalbumwizzard.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'createalbumwizzard.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 63
#error "This file was generated using the moc from 4.8.4. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_CreateAlbumWizzard[] = {

 // content:
       6,       // revision
       0,       // classname
       0,    0, // classinfo
      13,   14, // methods
       2,   79, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

 // slots: signature, parameters, type, tag, flags
      26,   20,   19,   19, 0x0a,
      59,   19,   19,   19, 0x0a,
      83,   19,   19,   19, 0x0a,
     113,  107,   19,   19, 0x0a,
     152,   19,   19,   19, 0x0a,
     180,   19,   19,   19, 0x0a,
     205,   19,   19,   19, 0x0a,
     237,  231,   19,   19, 0x0a,
     274,   19,   19,   19, 0x0a,
     315,  305,   19,   19, 0x0a,
     365,  346,   19,   19, 0x0a,
     388,  107,   19,   19, 0x0a,
     415,  107,   19,   19, 0x0a,

 // properties: name, type, flags
     438,  433, 0x01095103,
     456,  433, 0x01095103,

       0        // eod
};

static const char qt_meta_stringdata_CreateAlbumWizzard[] = {
    "CreateAlbumWizzard\0\0index\0"
    "on_tabWidget_currentChanged(int)\0"
    "on_nextButton_clicked()\0on_prevButton_clicked()\0"
    "value\0on_compressionSlider_valueChanged(int)\0"
    "on_locationButton_clicked()\0"
    "on_startButton_clicked()\0"
    "on_cancelButton_clicked()\0state\0"
    "on_archiveCheckBox_stateChanged(int)\0"
    "on_titleEdit_editingFinished()\0source,nr\0"
    "progress(ImageCollection*,int)\0"
    "completed,errorMsg\0finished(bool,QString)\0"
    "setChangesLostDialog(bool)\0setAboutTab(bool)\0"
    "bool\0changesLostDialog\0aboutTab\0"
};

void CreateAlbumWizzard::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        Q_ASSERT(staticMetaObject.cast(_o));
        CreateAlbumWizzard *_t = static_cast<CreateAlbumWizzard *>(_o);
        switch (_id) {
        case 0: _t->on_tabWidget_currentChanged((*reinterpret_cast< int(*)>(_a[1]))); break;
        case 1: _t->on_nextButton_clicked(); break;
        case 2: _t->on_prevButton_clicked(); break;
        case 3: _t->on_compressionSlider_valueChanged((*reinterpret_cast< int(*)>(_a[1]))); break;
        case 4: _t->on_locationButton_clicked(); break;
        case 5: _t->on_startButton_clicked(); break;
        case 6: _t->on_cancelButton_clicked(); break;
        case 7: _t->on_archiveCheckBox_stateChanged((*reinterpret_cast< int(*)>(_a[1]))); break;
        case 8: _t->on_titleEdit_editingFinished(); break;
        case 9: _t->progress((*reinterpret_cast< ImageCollection*(*)>(_a[1])),(*reinterpret_cast< int(*)>(_a[2]))); break;
        case 10: _t->finished((*reinterpret_cast< bool(*)>(_a[1])),(*reinterpret_cast< const QString(*)>(_a[2]))); break;
        case 11: _t->setChangesLostDialog((*reinterpret_cast< bool(*)>(_a[1]))); break;
        case 12: _t->setAboutTab((*reinterpret_cast< bool(*)>(_a[1]))); break;
        default: ;
        }
    }
}

const QMetaObjectExtraData CreateAlbumWizzard::staticMetaObjectExtraData = {
    0,  qt_static_metacall 
};

const QMetaObject CreateAlbumWizzard::staticMetaObject = {
    { &QDialog::staticMetaObject, qt_meta_stringdata_CreateAlbumWizzard,
      qt_meta_data_CreateAlbumWizzard, &staticMetaObjectExtraData }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &CreateAlbumWizzard::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *CreateAlbumWizzard::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *CreateAlbumWizzard::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_CreateAlbumWizzard))
        return static_cast<void*>(const_cast< CreateAlbumWizzard*>(this));
    return QDialog::qt_metacast(_clname);
}

int CreateAlbumWizzard::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QDialog::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 13)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 13;
    }
#ifndef QT_NO_PROPERTIES
      else if (_c == QMetaObject::ReadProperty) {
        void *_v = _a[0];
        switch (_id) {
        case 0: *reinterpret_cast< bool*>(_v) = hasChangesLostDialog(); break;
        case 1: *reinterpret_cast< bool*>(_v) = hasAboutTab(); break;
        }
        _id -= 2;
    } else if (_c == QMetaObject::WriteProperty) {
        void *_v = _a[0];
        switch (_id) {
        case 0: setChangesLostDialog(*reinterpret_cast< bool*>(_v)); break;
        case 1: setAboutTab(*reinterpret_cast< bool*>(_v)); break;
        }
        _id -= 2;
    } else if (_c == QMetaObject::ResetProperty) {
        _id -= 2;
    } else if (_c == QMetaObject::QueryPropertyDesignable) {
        _id -= 2;
    } else if (_c == QMetaObject::QueryPropertyScriptable) {
        _id -= 2;
    } else if (_c == QMetaObject::QueryPropertyStored) {
        _id -= 2;
    } else if (_c == QMetaObject::QueryPropertyEditable) {
        _id -= 2;
    } else if (_c == QMetaObject::QueryPropertyUser) {
        _id -= 2;
    }
#endif // QT_NO_PROPERTIES
    return _id;
}
QT_END_MOC_NAMESPACE
