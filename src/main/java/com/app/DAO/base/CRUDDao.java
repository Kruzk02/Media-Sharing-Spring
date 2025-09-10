package com.app.DAO.base;

public interface CRUDDao<T> extends Creatable<T>, Readable<T>, Updatable<T>, Deletable {}
