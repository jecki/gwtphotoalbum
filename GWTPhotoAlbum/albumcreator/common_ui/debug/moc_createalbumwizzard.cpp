/****************************************************************************
** Meta object code from reading C++ file 'createalbumwizzard.h'
**
** Created by: The Qt Meta Object Compiler version 67 (Qt 5.1.1)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../createalbumwizzard.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'createalbumwizzard.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 67
#error "This file was generated using the moc from 5.1.1. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
struct qt_meta_stringdata_CreateAlbumWizzard_t {
    QByteArrayData data[26];
    char stringdata[435];
};
#define QT_MOC_LITERAL(idx, ofs, len) \
    Q_STATIC_BYTE_ARRAY_DATA_HEADER_INITIALIZER_WITH_OFFSET(len, \
    offsetof(qt_meta_stringdata_CreateAlbumWizzard_t, stringdata) + ofs \
        - idx * sizeof(QByteArrayData) \
    )
static const qt_meta_stringdata_CreateAlbumWizzard_t qt_meta_stringdata_CreateAlbumWizzard = {
    {
QT_MOC_LITERAL(0, 0, 18),
QT_MOC_LITERAL(1, 19, 27),
QT_MOC_LITERAL(2, 47, 0),
QT_MOC_LITERAL(3, 48, 5),
QT_MOC_LITERAL(4, 54, 21),
QT_MOC_LITERAL(5, 76, 21),
QT_MOC_LITERAL(6, 98, 33),
QT_MOC_LITERAL(7, 132, 5),
QT_MOC_LITERAL(8, 138, 25),
QT_MOC_LITERAL(9, 164, 31),
QT_MOC_LITERAL(10, 196, 22),
QT_MOC_LITERAL(11, 219, 23),
QT_MOC_LITERAL(12, 243, 31),
QT_MOC_LITERAL(13, 275, 5),
QT_MOC_LITERAL(14, 281, 28),
QT_MOC_LITERAL(15, 310, 8),
QT_MOC_LITERAL(16, 319, 16),
QT_MOC_LITERAL(17, 336, 6),
QT_MOC_LITERAL(18, 343, 2),
QT_MOC_LITERAL(19, 346, 8),
QT_MOC_LITERAL(20, 355, 9),
QT_MOC_LITERAL(21, 365, 8),
QT_MOC_LITERAL(22, 374, 20),
QT_MOC_LITERAL(23, 395, 11),
QT_MOC_LITERAL(24, 407, 17),
QT_MOC_LITERAL(25, 425, 8)
    },
    "CreateAlbumWizzard\0on_tabWidget_currentChanged\0"
    "\0index\0on_nextButton_clicked\0"
    "on_prevButton_clicked\0"
    "on_compressionSlider_valueChanged\0"
    "value\0on_locationButton_clicked\0"
    "on_locationEdit_editingFinished\0"
    "on_startButton_clicked\0on_cancelButton_clicked\0"
    "on_archiveCheckBox_stateChanged\0state\0"
    "on_titleEdit_editingFinished\0progress\0"
    "ImageCollection*\0source\0nr\0finished\0"
    "completed\0errorMsg\0setChangesLostDialog\0"
    "setAboutTab\0changesLostDialog\0aboutTab\0"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_CreateAlbumWizzard[] = {

 // content:
       7,       // revision
       0,       // classname
       0,    0, // classinfo
      14,   14, // methods
       2,  116, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

 // slots: name, argc, parameters, tag, flags
       1,    1,   84,    2, 0x0a,
       4,    0,   87,    2, 0x0a,
       5,    0,   88,    2, 0x0a,
       6,    1,   89,    2, 0x0a,
       8,    0,   92,    2, 0x0a,
       9,    0,   93,    2, 0x0a,
      10,    0,   94,    2, 0x0a,
      11,    0,   95,    2, 0x0a,
      12,    1,   96,    2, 0x0a,
      14,    0,   99,    2, 0x0a,
      15,    2,  100,    2, 0x0a,
      19,    2,  105,    2, 0x0a,
      22,    1,  110,    2, 0x0a,
      23,    1,  113,    2, 0x0a,

 // slots: parameters
    QMetaType::Void, QMetaType::Int,    3,
    QMetaType::Void,
    QMetaType::Void,
    QMetaType::Void, QMetaType::Int,    7,
    QMetaType::Void,
    QMetaType::Void,
    QMetaType::Void,
    QMetaType::Void,
    QMetaType::Void, QMetaType::Int,   13,
    QMetaType::Void,
    QMetaType::Void, 0x80000000 | 16, QMetaType::Int,   17,   18,
    QMetaType::Void, QMetaType::Bool, QMetaType::QString,   20,   21,
    QMetaType::Void, QMetaType::Bool,    7,
    QMetaType::Void, QMetaType::Bool,    7,

 // properties: name, type, flags
      24, QMetaType::Bool, 0x00095103,
      25, QMetaType::Bool, 0x00095103,

       0        // eod
};

void CreateAlbumWizzard::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        CreateAlbumWizzard *_t = static_cast<CreateAlbumWizzard *>(_o);
        switch (_id) {
        case 0: _t->on_tabWidget_currentChanged((*reinterpret_cast< int(*)>(_a[1]))); break;
        case 1: _t->on_nextButton_clicked(); break;
        case 2: _t->on_prevButton_clicked(); break;
        case 3: _t->on_compressionSlider_valueChanged((*reinterpret_cast< int(*)>(_a[1]))); break;
        case 4: _t->on_locationButton_clicked(); break;
        case 5: _t->on_locationEdit_editingFinished(); break;
        case 6: _t->on_startButton_clicked(); break;
        case 7: _t->on_cancelButton_clicked(); break;
        case 8: _t->on_archiveCheckBox_stateChanged((*reinterpret_cast< int(*)>(_a[1]))); break;
        case 9: _t->on_titleEdit_editingFinished(); break;
        case 10: _t->progress((*reinterpret_cast< ImageCollection*(*)>(_a[1])),(*reinterpret_cast< int(*)>(_a[2]))); break;
        case 11: _t->finished((*reinterpret_cast< bool(*)>(_a[1])),(*reinterpret_cast< const QString(*)>(_a[2]))); break;
        case 12: _t->setChangesLostDialog((*reinterpret_cast< bool(*)>(_a[1]))); break;
        case 13: _t->setAboutTab((*reinterpret_cast< bool(*)>(_a[1]))); break;
        default: ;
        }
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        switch (_id) {
        default: *reinterpret_cast<int*>(_a[0]) = -1; break;
        case 10:
            switch (*reinterpret_cast<int*>(_a[1])) {
            default: *reinterpret_cast<int*>(_a[0]) = -1; break;
            case 0:
                *reinterpret_cast<int*>(_a[0]) = qRegisterMetaType< ImageCollection* >(); break;
            }
            break;
        }
    }
}

const QMetaObject CreateAlbumWizzard::staticMetaObject = {
    { &QDialog::staticMetaObject, qt_meta_stringdata_CreateAlbumWizzard.data,
      qt_meta_data_CreateAlbumWizzard,  qt_static_metacall, 0, 0}
};


const QMetaObject *CreateAlbumWizzard::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *CreateAlbumWizzard::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_CreateAlbumWizzard.stringdata))
        return static_cast<void*>(const_cast< CreateAlbumWizzard*>(this));
    return QDialog::qt_metacast(_clname);
}

int CreateAlbumWizzard::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QDialog::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 14)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 14;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 14)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 14;
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
    } else if (_c == QMetaObject::RegisterPropertyMetaType) {
        if (_id < 2)
            *reinterpret_cast<int*>(_a[0]) = -1;
        _id -= 2;
    }
#endif // QT_NO_PROPERTIES
    return _id;
}
QT_END_MOC_NAMESPACE
