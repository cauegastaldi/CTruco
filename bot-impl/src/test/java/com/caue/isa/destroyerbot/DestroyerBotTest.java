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

import com.bueno.spi.model.GameIntel;
import com.bueno.spi.service.BotServiceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}