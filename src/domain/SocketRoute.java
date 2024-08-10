/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Kerolos Raouf
 */
public enum SocketRoute {
    LOG_IN,
    LOG_IN_RESPONSE,
    SIGN_UP,
    SIGN_UP_RESPONSE,
    PLAYER_MOVE,
    AVAILABLE_PLAYERS,
    LOG_OUT,
    ALL_PLAYERS,
    SURRENDER,
    LEAVE_MATCH,
    INCREMENT_SCORE,
    REQUEST_TO_PLAY,
    RESPONSE_TO_REQUEST_TO_PLAY,
    DIALOG_REQUEST_TO_PLAY,
    WAITING_REQUEST_TO_PLAY,
    SCORE_BOARD,
    CHECK_SERVER,
    ERROR_OCCURED,
    PLAY_AGAIN,
}
