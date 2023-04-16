/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.caue.isa.destroyerbot;

import com.bueno.spi.model.*;
import com.bueno.spi.service.BotServiceProvider;

import java.util.Optional;

public class DestroyerBot implements BotServiceProvider {
    @Override
    public int getRaiseResponse(GameIntel intel) {
        if (intel.getHandPoints() == 9 && intel.getOpponentScore() >= 3)
            return 0;
        if(hasStrongestManilhas(intel)) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean getMaoDeOnzeResponse(GameIntel intel) {
        if (hasAtLeastOneManilhaAndTwoCardsAboveAceRank(intel))
            return true;
        return false;
    }

    private boolean isMaoDeOnze(GameIntel intel){
        int opponentScore = intel.getOpponentScore();
        int myScore = intel.getScore();
        if (opponentScore == 11 || myScore == 11) {
            return true;
        }
        return false;
    }

    @Override
    public boolean decideIfRaises(GameIntel intel) {
        if (isMaoDeOnze(intel)) return false;
        if(getScoreDifference(intel) >= -6) return false;
        return true;
    }

    @Override
    public CardToPlay chooseCard(GameIntel intel) {
        if (intel.getOpponentCard().isPresent()) {
            Optional<TrucoCard> strongerCard = getCardStrongerThanOpponentOne(intel);
            Optional<TrucoCard> equalCard = getWeakestCardEqualsToOpponentCard(intel);
            Optional<TrucoCard> weakestCard = getWeakestCard(intel);
            if (strongerCard.isPresent())
                return CardToPlay.of(strongerCard.get());
            if (equalCard.isPresent())
                return CardToPlay.of(equalCard.get());
            return CardToPlay.of(weakestCard.get());
        }
        return CardToPlay.of(intel.getCards().get(0));
    }

    private Optional<TrucoCard> getCardStrongerThanOpponentOne(GameIntel intel) {
        TrucoCard opponentCard = intel.getOpponentCard().get();
        TrucoCard vira = intel.getVira();
        return intel.getCards().stream()
                .filter(card -> card.compareValueTo(opponentCard, vira) > 0)
                .min((card1, card2) ->
                        card1.compareValueTo(card2, vira));
    }

    private Optional<TrucoCard> getWeakestCard(GameIntel intel) {
        TrucoCard vira = intel.getVira();
        return intel.getCards().stream()
                .min((card1, card2) ->
                        card1.compareValueTo(card2, vira));
    }

    private Optional<TrucoCard> getWeakestCardEqualsToOpponentCard(GameIntel intel) {
        TrucoCard vira = intel.getVira();
        TrucoCard opponentCard = intel.getOpponentCard().get();
        return intel.getCards().stream()
                .filter(card -> card.compareValueTo(opponentCard, vira) == 0)
                .min((card1, card2) ->
                        card1.compareValueTo(card2, vira));

    }

    private boolean hasStrongestManilhas(GameIntel intel) {
        TrucoCard vira = intel.getVira();
        TrucoCard zap = TrucoCard.of(vira.getRank().next(), CardSuit.CLUBS);
        TrucoCard copas = TrucoCard.of(vira.getRank().next(), CardSuit.HEARTS);
        if(intel.getCards().contains(zap) && intel.getCards().contains(copas)) {
            return true;
        }
        return false;
    }

    private int getScoreDifference(GameIntel intel){
        int scoreDifference = intel.getScore() - intel.getOpponentScore();
        return scoreDifference;
    }

    private boolean hasAtLeastOneManilhaAndTwoCardsAboveAceRank(GameIntel intel) {
        TrucoCard vira = intel.getVira();
        return intel.getCards().stream()
                .filter(card -> card.isManilha(vira) || card.getRank().equals(CardRank.TWO)
                        || card.getRank().equals(CardRank.THREE))
                .toList()
                .size() == 3;
    }
}
