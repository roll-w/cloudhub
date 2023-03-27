/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huel.cloudhub.client.disk.configuration;

import org.huel.cloudhub.common.ErrorCodeFinderChain;
import org.huel.cloudhub.common.ErrorCodeMessageProvider;
import org.huel.cloudhub.common.IoErrorCode;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.DataErrorCode;
import org.huel.cloudhub.web.ErrorCodeMessageProviderImpl;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
public class ErrorCodeConfiguration {

    @Bean
    public ErrorCodeMessageProvider errorCodeMessageProvider(MessageSource messageSource) {
        return new ErrorCodeMessageProviderImpl(messageSource);
    }

    @Bean
    public ErrorCodeFinderChain errorCodeFinderChain() {
        return ErrorCodeFinderChain.start(
                WebCommonErrorCode.getFinderInstance(),
                AuthErrorCode.getFinderInstance(),
                DataErrorCode.getFinderInstance(),
                IoErrorCode.getFinderInstance(),
                UserErrorCode.getFinderInstance()
        );
    }

}
