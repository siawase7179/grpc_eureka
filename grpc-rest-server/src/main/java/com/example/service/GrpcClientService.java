package com.example.service;

import org.springframework.stereotype.Service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import net.grpc.lib.TokenReply;
import net.grpc.lib.TokenRequest;
import net.grpc.lib.TokenGrpc.TokenBlockingStub;


@Service
public class GrpcClientService {
    @GrpcClient("grpc-auth-server")
    private TokenBlockingStub tokenBlockingStub;

    public TokenReply requestToken(final TokenRequest request){
        final TokenReply response = tokenBlockingStub.requestToken(request);
        return response;
    }

}
