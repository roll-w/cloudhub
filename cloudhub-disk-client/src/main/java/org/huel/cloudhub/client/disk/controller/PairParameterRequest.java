package org.huel.cloudhub.client.disk.controller;

/**
 * @author RollW
 */
public record PairParameterRequest<F, S>(
        F first,
        S second
) {
}
