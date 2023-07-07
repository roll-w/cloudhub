package org.huel.cloudhub.client.disk.controller;

/**
 * @author RollW
 */
public record OneParameterRequest<T>(
        T value
) {
}
