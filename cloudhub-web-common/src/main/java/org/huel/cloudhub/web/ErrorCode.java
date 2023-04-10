package org.huel.cloudhub.web;

import space.lingu.NonNull;

import java.io.Serializable;

/**
 * <h2>Error Code</h2>
 *
 * <h3>Error Code Naming Format</h3>
 * <ul>
 *     <li>Success: {@code SUCCESS}</li>
 *     <li>Error: {@code ERROR_{Specify error name}}, like {@code ERROR_FILE_NOT_EXIST}.</li>
 * </ul>
 * <p>
 * Class naming format:
 *  {@code {Group}ErrorCode}, like {@code FileErrorCode}.
 * <p>
 * This affects the generation of the i18n key.
 * <p>
 * The final key format is: {@code error.{Group}.{Specify error name}},
 * like {@code error.file.file_not_exist}.
 *
 * @author RollW
 */
public interface ErrorCode extends Serializable {
    String SUCCESS_CODE = "00000";

    @NonNull
    String getCode();

    @NonNull
    String getName();

    boolean success();

    default boolean failed() {
        return !success();
    }

    int getStatus();

    String toString();
}

