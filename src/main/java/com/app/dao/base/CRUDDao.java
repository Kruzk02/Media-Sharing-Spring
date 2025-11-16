package com.app.dao.base;

public interface CRUDDao<T> extends Creatable<T>, Readable<T>, Updatable<T>, Deletable {}
