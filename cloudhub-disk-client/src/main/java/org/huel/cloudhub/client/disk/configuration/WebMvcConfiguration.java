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

import com.fasterxml.jackson.databind.MapperFeature;
import org.huel.cloudhub.client.disk.configuration.json.ErrorCodeDeserializer;
import org.huel.cloudhub.client.disk.configuration.json.ErrorCodeSerializer;
import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.ErrorCodeFinderChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author RollW
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(ErrorCodeFinderChain finderChain) {
        ErrorCodeDeserializer errorCodeDeserializer = new ErrorCodeDeserializer(finderChain);
        ErrorCodeSerializer errorCodeSerializer = new ErrorCodeSerializer();

        return Jackson2ObjectMapperBuilder
                .json()
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .serializerByType(ErrorCode.class, errorCodeSerializer)
                .deserializerByType(ErrorCode.class, errorCodeDeserializer);
    }

}
