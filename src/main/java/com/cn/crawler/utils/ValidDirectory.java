package com.cn.crawler.utils;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

/**
 * Created by burhan on 9/13/17.
 */
public class ValidDirectory implements IParameterValidator {
    public void validate(String name, String value)
            throws ParameterException {
        File folder = new File(value);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new ParameterException("Parameter " + name + " should be a valid directory (found " + value +")");
        }
    }
}
