/*
 * Copyright (C) 2022 Lingu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huel.cloudhub.file.fs;

import java.io.File;

/**
 * @author RollW
 */
public class LocalFileProvider implements ServerFileProvider {

    @Override
    public ServerFile openFile(String path) {
        return new LocalFile(new File(path));
    }

    @Override
    public ServerFile openFile(String parent, String path) {
        return new LocalFile(new File(parent, path));
    }

    @Override
    public ServerFile openFile(ServerFile parent, String path) {
        return new LocalFile(new File(parent.getPath(), path));
    }
}
