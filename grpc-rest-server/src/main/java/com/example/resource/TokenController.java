package com.example.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.GrpcClientService;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import net.grpc.lib.TokenReply;
import net.grpc.lib.TokenRequest;


@RestController
@RequestMapping(value = "/v1")
public class TokenController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenController.class);
    private final GrpcClientService grpcClientService;

    @Autowired
    public TokenController (GrpcClientService grpcClientService){
        this.grpcClientService = grpcClientService;
    }

    @RequestMapping(
        value = "/token",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    public String requestToken(@RequestHeader("X-Client-Id")String id, @RequestHeader("X-Client-Password")String password) throws IOException {
        TokenRequest tokenRequest = TokenRequest.newBuilder().setId(id).setPassword(password).build();
        TokenReply tokenReply = grpcClientService.requestToken(tokenRequest);
        return toJson(tokenReply.toBuilder());
    }
    public static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
        return JsonFormat.printer().print(messageOrBuilder);
    }

}
