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
        TrucoCard vira;
        List<TrucoCard> cards;

        @Test
        @DisplayName("Should accept raise request if it is a doze and opponent score is greater than two")
        void shouldAcceptRaiseRequestIfItIsDozeAndOpponentScoreIsGreaterThanTwo() {
            when(intel.getHandPoints()).thenReturn(9);
            when(intel.getOpponentScore()).thenReturn(3);
            assertThat(sut.getRaiseResponse(intel)).isEqualTo(0);
        }

        @Test
        @DisplayName("Should respond with point raise if request is a 'seis' and opponent score is greater than " +
                "eight")
        void shouldReRaisePointRaiseRequestIfIsSeisAndOpponentScoreIsGreaterThanEight(){
            when(intel.getHandPoints()).thenReturn(3);
            when(intel.getOpponentScore()).thenReturn(10);

            assertThat(sut.getRaiseResponse(intel)).isEqualTo(1);
        }

        @Test
        @DisplayName("Should respond with point raise if has strongest manilhas")
        void shouldReRaisePointRaiseRequestIfItHasTheTwoStrongestManilhas(){
            vira = TrucoCard.of(CardRank.KING, CardSuit.SPADES);
            cards = List.of(TrucoCard.of(CardRank.ACE, CardSuit.CLUBS),
                    TrucoCard.of(CardRank.ACE, CardSuit.HEARTS),
                    TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS));

            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);

            assertThat(sut.getRaiseResponse(intel)).isEqualTo(1);
        }

        @Test
        @DisplayName("Should accept point raise request if it has at least two cards with " +
                     "rank greater than 'two' rank")
        void shouldAcceptPointRaiseRequestIfItHasAtLeastTwoCardsWithRankGreaterThanTwo(){
            vira = TrucoCard.of(CardRank.KING, CardSuit.SPADES);
            cards = List.of(TrucoCard.of(CardRank.THREE, CardSuit.CLUBS),
                    TrucoCard.of(CardRank.THREE, CardSuit.HEARTS),
                    TrucoCard.of(CardRank.JACK, CardSuit.DIAMONDS));

            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);

            assertThat(sut.getRaiseResponse(intel)).isEqualTo(0);
        }

        @Test
        @DisplayName("Should respond with point raise if opponent score is equal to eleven")
        void shouldReRaisePointRaiseRequestIfOpponentScoreIsEqualToEleven(){
            when(intel.getOpponentScore()).thenReturn(11);

            assertThat(sut.getRaiseResponse(intel)).isEqualTo(1);
        }

        @Test
        @DisplayName("Should not accept or re-raise point raise requests if bot score is equal to eleven and " +
                     "it will not lose the game")
        void shouldNotAcceptOrReRaiseRaiseRequestIfScoreIsEqualToEleven(){
            when(intel.getScore()).thenReturn(11);

            assertThat(sut.getRaiseResponse(intel)).isEqualTo(-1);
        }
    }

    @Nested @DisplayName("When playing a card")
    class ChooseCardTest {
        TrucoCard vira;
        List<TrucoCard> cards;
        Optional<TrucoCard> opponentCard;

        Optional<TrucoCard> opponentCard2;

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

        @Test
        @DisplayName("Should play a king, ace or two (in this order) in the first round (only if they are not manilhas and if it is the first to play)")
        void shouldPlayAKingOrAceOrTwoInTheFirstRoundIfItIsTheFirstToPlay(){
            vira = TrucoCard.of(CardRank.ACE, CardSuit.SPADES);

            cards = List.of(TrucoCard.of(CardRank.KING, CardSuit.SPADES),
                    TrucoCard.of(CardRank.TWO, CardSuit.DIAMONDS),
                    TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS));
            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);

            assertThat(sut.chooseCard(intel).content()).isEqualTo(TrucoCard.of(CardRank.KING, CardSuit.SPADES));
        }

        @Test
        @DisplayName("Should play the lowest rank card in the first round if it has three manilhas")
        void shouldPlayTheLowestRankManilhaInTheFirstRoundIfItHasThreeManilhas(){
            vira = TrucoCard.of(CardRank.THREE, CardSuit.HEARTS);

            cards = List.of(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES),
                    TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS),
                    TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS));
            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);

            when(intel.getRoundResults()).thenReturn(List.of());
            assertThat(sut.chooseCard(intel).content()).isEqualTo(TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS));
        }


        @Test
        @DisplayName("Should play the medium rank card in the second round if it has three manilhas")
        void shouldPlayTheMediumRankManilhaInTheSecondRoundIfItHasThreeManilhas(){
            vira = TrucoCard.of(CardRank.THREE, CardSuit.HEARTS);

            cards = List.of(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES),
                    TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS),
                    TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS));

            opponentCard = Optional.of(TrucoCard.of(CardRank.KING, CardSuit.HEARTS));
            opponentCard2 = Optional.of(TrucoCard.of(CardRank.ACE, CardSuit.SPADES));

            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);

            when(intel.getRoundResults()).thenReturn(List.of(GameIntel.RoundResult.WON));
            assertThat(sut.chooseCard(intel).value()).isEqualTo(CardToPlay.discard(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES)).value());
        }

        @Test
        @DisplayName("Should play the highest rank card in the third round if it has three manilhas")
        void shouldPlayTheHighestRankManilhaInTheThirdRoundIfItHasThreeManilhas(){
            vira = TrucoCard.of(CardRank.THREE, CardSuit.HEARTS);

            cards = List.of(TrucoCard.of(CardRank.FOUR, CardSuit.SPADES),
                    TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS),
                    TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS));


            when(intel.getVira()).thenReturn(vira);
            when(intel.getCards()).thenReturn(cards);

            when(intel.getRoundResults()).thenReturn(List.of(GameIntel.RoundResult.DREW, GameIntel.RoundResult.DREW));
            assertThat(sut.chooseCard(intel).content()).isEqualTo(TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS));
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
        @DisplayName("Should not ask for point raises during 'mao de onze' turns.")
        void shouldNotAskForPointRaiseDuringMaoDeOnze(){
            when(intel.getScore()).thenReturn(11);
            when(intel.getOpponentScore()).thenReturn(11);
            assertThat(sut.decideIfRaises(intel)).isFalse();
        }

        @Test
        @DisplayName("Should not ask for point raise if the opponent is winning by 6 or more points.")
        void shouldNotAskForPointRaiseIfOpponentIsWinningBySixOrMorePoints(){
            when(intel.getScore()).thenReturn(1);
            when(intel.getOpponentScore()).thenReturn(10);
            assertThat(sut.decideIfRaises(intel)).isFalse();
        }
    }

    @Nested
    @DisplayName("When get a 'mao de onze' request")
    class GetMaoDeOnzeResponseTest {
        List<TrucoCard> cards;
        TrucoCard vira;

        @Test
        @DisplayName("Should play mao de onze if has, at least, one manilha and two cards above ace rank")
        void shouldPlayMaoDeOnzeIfHasAtLeastOneManilhaAndTwoCardsAboveAceRank() {
            vira = TrucoCard.of(CardRank.JACK, CardSuit.HEARTS);
            cards = List.of(TrucoCard.of(CardRank.KING, CardSuit.DIAMONDS),
                    TrucoCard.of(CardRank.TWO, CardSuit.HEARTS),
                    TrucoCard.of(CardRank.THREE, CardSuit.CLUBS));

            when(intel.getCards()).thenReturn(cards);
            when(intel.getVira()).thenReturn(vira);

            assertThat(sut.getMaoDeOnzeResponse(intel)).isTrue();
        }
    }
}