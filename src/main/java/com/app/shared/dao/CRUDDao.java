package com.app.shared.dao;

public interface CRUDDao<T> extends Creatable<T>, Readable<T>, Updatable<T>, Deletable {}
