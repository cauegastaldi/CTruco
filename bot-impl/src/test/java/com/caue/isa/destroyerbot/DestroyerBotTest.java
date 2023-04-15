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

import com.bueno.spi.model.CardRank;
import com.bueno.spi.model.CardSuit;
import com.bueno.spi.model.GameIntel;
import com.bueno.spi.model.TrucoCard;
import com.bueno.spi.service.BotServiceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestroyerBotTest {

    @Mock
    private GameIntel intel;

    private final BotServiceProvider sut = new DestroyerBot();

    @Nested @DisplayName("When get a raise request")
    class GetRaiseResponseTest {
        @Test
        @DisplayName("Should accept raise request if it is a doze and opponent score is greater than two")
        void shouldAcceptRaiseRequestIfItIsDozeAndOpponentScoreIsGreaterThanTwo() {
            when(intel.getHandPoints()).thenReturn(9);
            when(intel.getOpponentScore()).thenReturn(3);
            assertThat(sut.getRaiseResponse(intel)).isEqualTo(0);
        }
    }

    @Nested @DisplayName("When playing a card")
    class ChooseCardTest {
        List<TrucoCard> cards;
        Optional<TrucoCard> opponentCard;

        @Test
        @DisplayName("Should play the weakest card between the strongest ones than the opponent one")
        void shouldPlayTheWeakestCardBetweenTheStrongestOnesThanOpponentOne() {
            cards = List.of(TrucoCard.of(CardRank.KING, CardSuit.HEARTS),
                            TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS));
            opponentCard = Optional.of(TrucoCard.of(CardRank.SEVEN, CardSuit.CLUBS));
            when(intel.getVira()).thenReturn(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES));
            when(intel.getOpponentCard()).thenReturn(opponentCard);
            when(intel.getCards()).thenReturn(cards);

            assertThat(sut.chooseCard(intel).content())
                    .isEqualTo(TrucoCard.of(CardRank.KING, CardSuit.HEARTS));
        }

        @Test
        @DisplayName("Should play the card that is equal to the opponent card if it doesn't have a stronger card" +
                     "than the opponent card")
        void shouldPlayTheCardThatIsEqualToTheOpponentCardIfDoesNotHaveAStrongerCardThanTheOpponentCard() {
            cards = List.of(TrucoCard.of(CardRank.SEVEN, CardSuit.DIAMONDS),
                    TrucoCard.of(CardRank.TWO, CardSuit.HEARTS),
                    TrucoCard.of(CardRank.KING, CardSuit.SPADES));

            opponentCard = Optional.of(TrucoCard.of(CardRank.TWO, CardSuit.CLUBS));

            when(intel.getVira()).thenReturn(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES));
            when(intel.getOpponentCard()).thenReturn(opponentCard);
            when(intel.getCards()).thenReturn(cards);

            assertThat(sut.chooseCard(intel).content())
                    .isEqualTo(TrucoCard.of(CardRank.TWO, CardSuit.HEARTS));
        }

        @Test
        @DisplayName("Should play the weakest card if doesn't have a card stronger than the opponent card")
        void shouldPlayTheWeakestCardIfDoesNotHaveAStrongerCardThanTheOpponentCard() {
            cards = List.of(TrucoCard.of(CardRank.SEVEN, CardSuit.DIAMONDS),
                            TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS),
                            TrucoCard.of(CardRank.SIX, CardSuit.SPADES));

            opponentCard = Optional.of(TrucoCard.of(CardRank.KING, CardSuit.CLUBS));

            when(intel.getVira()).thenReturn(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES));
            when(intel.getOpponentCard()).thenReturn(opponentCard);
            when(intel.getCards()).thenReturn(cards);

            assertThat(sut.chooseCard(intel).content())
                    .isEqualTo(TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS));
        }
    }
    @Nested
    @DisplayName("When requesting a point raise")
    class DecidesIfRaisesTest {
        TrucoCard vira;
        List<TrucoCard> cards;
        List<GameIntel.RoundResult> results;
        Optional<TrucoCard> opponentCard;

        @Test
        @DisplayName("Should not request a point raise if bot will lose the hand")
        void shouldNotRequestAPointRaiseIfWillLoseTheHand() {
            results = List.of(GameIntel.RoundResult.LOST, GameIntel.RoundResult.WON);
            vira = TrucoCard.of(CardRank.FIVE, CardSuit.HEARTS);
            cards = List.of(TrucoCard.of(CardRank.SEVEN, CardSuit.DIAMONDS),
                            TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS),
                            TrucoCard.of(CardRank.FIVE, CardSuit.SPADES));

            opponentCard = Optional.of(TrucoCard.of(CardRank.SIX, CardSuit.CLUBS));

            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);
            when(intel.getOpponentCard()).thenReturn(opponentCard);
            when(intel.getRoundResults()).thenReturn(results);

            assertThat(sut.decideIfRaises(intel)).isFalse();
        }

        @Test
        @DisplayName("Should not ask for point raises during 'mao de onze' turns.")
        void shouldNotAskForPointRaiseDuringMaoDeOnze(){
            when(intel.getScore()).thenReturn(11);
            when(intel.getOpponentScore()).thenReturn(11);
            assertThat(sut.decideIfRaises(intel)).isFalse();
        }
    }


}