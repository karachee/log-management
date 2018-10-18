package com.karachee.lms.utils;

import java.util.ArrayList;

public class NoNullArrayList<E> extends ArrayList<E> {

    public NoNullArrayList() {
        super();
    }

    @Override
    public boolean add(E e) {
        return (e != null) ? super.add(e) : false;
    }
}
