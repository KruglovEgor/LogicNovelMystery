package ru.itmo.lnm.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.lnm.backend.dto.CampaignReportDto;
import ru.itmo.lnm.backend.dto.SessionDto;
import ru.itmo.lnm.backend.messages.CampaignReportResponse;
import ru.itmo.lnm.backend.messages.StateResponse;
import ru.itmo.lnm.backend.model.*;
import ru.itmo.lnm.backend.repository.LeaderBoardRepository;
import ru.itmo.lnm.backend.repository.SessionRepository;
import ru.itmo.lnm.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class StateService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final LeaderBoardRepository leaderBoardRepository;

    public CampaignReportResponse campaignReport(CampaignReportDto request, String username) {
        User user = userRepository.findByUsername(username);
        Session session = sessionRepository.findBySessionTokenAndUser(request.getSessionId(), user);
        Session partnerSession = null;
        String ending = null;
        if (session.getHero().equals(LnmHero.STEVE)) {
            ending = request.isWinner() ? "1" : "0";
        } else {
            partnerSession = sessionRepository.findBySessionTokenAndUserNot(request.getSessionId(), user);
            LnmPlayerState partnerPlayerState = partnerSession.getPlayerState();
            if (session.getHero().equals(LnmHero.PROFESSOR)) {
                if (request.isWinner()) {
                    switch (partnerPlayerState) {
                        case PLAYING -> ending = "1A";
                        case WAITING_LOST -> ending = "4A";
                        case WAITING_WON -> ending = "5A";
                        default -> {
                            return null;
                        }
                    }
                } else {
                    switch (partnerPlayerState) {
                        case PLAYING -> ending = "0A";
                        case WAITING_LOST -> ending = "2A";
                        case WAITING_WON -> ending = "3A";
                        default -> {
                            return null;
                        }
                    }
                }

            } else if (session.getHero().equals(LnmHero.VICKY)) {
                if (request.isWinner()) {
                    switch (partnerPlayerState) {
                        case PLAYING -> ending = "1B";
                        case WAITING_LOST -> ending = "4B";
                        case WAITING_WON -> ending = "5B";
                        default -> {
                            return null;
                        }
                    }
                } else {
                    switch (partnerPlayerState) {
                        case PLAYING -> ending = "0B";
                        case WAITING_LOST -> ending = "2B";
                        case WAITING_WON -> ending = "3B";
                        default -> {
                            return null;
                        }
                    }
                }
            }
        }
        changePlayerState(session, partnerSession, ending);
        return CampaignReportResponse.builder()
                .endingId(ending)
                .build();
    }

    public void changePlayerState(Session session, Session partnerSession, String ending){
        if (ending != null){
            try {
                if (ending.equals("0")) session.setPlayerState(LnmPlayerState.COMPLETED_LOST);
                else if (ending.equals("1")) session.setPlayerState(LnmPlayerState.COMPLETED_WON);
                else {
                    int startPart = ending.charAt(0);
                    switch (startPart) {
                        case 0 -> session.setPlayerState(LnmPlayerState.WAITING_LOST);
                        case 1 -> session.setPlayerState(LnmPlayerState.WAITING_WON);
                        case 2 -> {
                            session.setPlayerState(LnmPlayerState.COMPLETED_LOST);
                            partnerSession.setPlayerState(LnmPlayerState.COMPLETED_LOST);
                        }
                        case 3 -> {
                            session.setPlayerState(LnmPlayerState.COMPLETED_LOST);
                            partnerSession.setPlayerState(LnmPlayerState.COMPLETED_WON);
                        }
                        case 4 -> {
                            session.setPlayerState(LnmPlayerState.COMPLETED_WON);
                            partnerSession.setPlayerState(LnmPlayerState.COMPLETED_LOST);
                        }
                        case 5 -> {
                            session.setPlayerState(LnmPlayerState.COMPLETED_WON);
                            partnerSession.setPlayerState(LnmPlayerState.COMPLETED_WON);
                        }
                    }
                    sessionRepository.save(partnerSession);
                }
                sessionRepository.save(session);
            }catch (Exception e){
                System.err.println("Some exception in changePlayerState " + e);
            }
        }
    }
    public StateResponse receivePlayerState(SessionDto request, String username) {
        try {

            User user = userRepository.findByUsername(username);
            Session session = sessionRepository.findBySessionTokenAndUser(request.getSessionToken(), user);
            String partnerName = null;
            LnmResult result = null;
            Integer score = null;
            Integer partnerScore = null;
            LeaderBoard leaderBoard = null;
            Integer highScore = null;
            LnmPlayerState playerState = session.getPlayerState();
            score = session.getCurrentScore();
            if (session.getHero().equals(LnmHero.STEVE)) {
                if (playerState.equals(LnmPlayerState.COMPLETED_WON)) {
                    result = LnmResult.SINGLE_GOOD;
                } else if (playerState.equals(LnmPlayerState.COMPLETED_LOST)) {
                    result = LnmResult.SINGLE_BAD;
                }
                leaderBoard = leaderBoardRepository.findByUserAndGameMode(user, false);

            } else {
                Session partnerSession = sessionRepository.findBySessionTokenAndUserNot(request.getSessionToken(), user);
                partnerName = partnerSession.getUser().getUsername();
                LnmPlayerState partnerState = partnerSession.getPlayerState();
                if (playerState.equals(LnmPlayerState.COMPLETED_WON) && partnerState.equals(LnmPlayerState.COMPLETED_WON)) {
                    result = LnmResult.DOUBLE_GOOD;
                } else if (playerState.equals(LnmPlayerState.COMPLETED_WON) && partnerState.equals(LnmPlayerState.COMPLETED_LOST)) {
                    result = LnmResult.DOUBLE_AVERAGE;
                } else if (playerState.equals(LnmPlayerState.COMPLETED_LOST) && partnerState.equals(LnmPlayerState.COMPLETED_LOST)) {
                    result = LnmResult.DOUBLE_BAD;
                }
                partnerScore = partnerSession.getCurrentScore();
                leaderBoard = leaderBoardRepository.findByUserAndGameMode(user, true);

            }
            if (leaderBoard != null) {
                highScore = leaderBoard.getScore();
                if (score > highScore) {
                    leaderBoard.setScore(score);
                    leaderBoard.setSessionToken(session.getSessionToken());
                    leaderBoardRepository.save(leaderBoard);
                }
            } else {
                LeaderBoard newLeaderBoard = new LeaderBoard();
                newLeaderBoard.setUser(user);
                newLeaderBoard.setScore(score);
                newLeaderBoard.setSessionToken(session.getSessionToken());
                newLeaderBoard.setGameMode(partnerName != null);
                leaderBoardRepository.save(newLeaderBoard);
            }

            return StateResponse.builder()
                    .playerState(playerState)
                    .result(result)
                    .partnerName(partnerName)
                    .score(score)
                    .highScore(highScore)
                    .build();
        }catch (Exception e){
            System.err.println("Some problem in receiveState " + e);
            return null;
        }
    }

}
