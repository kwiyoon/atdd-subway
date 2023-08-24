package kuit.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kuit.subway.AcceptanceTest;
import kuit.subway.request.line.CreateLineRequest;
import kuit.subway.request.line.UpdateLineRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static kuit.subway.line.LineFixture.지하철_2호선_생성;
import static kuit.subway.line.LineFixture.*;
import static kuit.subway.line.LineStep.*;
import static kuit.subway.station.StationFixture.*;
import static kuit.subway.station.StationStep.지하철_역_생성_요청;
import static kuit.subway.utils.BaseResponseStatus.*;
import static kuit.subway.utils.RestAssuredUtil.get요청;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineTest extends AcceptanceTest {
    private final String ID_PATH = "result.id";
    private final String NAME_PATH = "result.name";
    private static final String ID_PATH = "result.id";
    private static final String NAME_PATH = "result.name";
    private static final String RESPONSECODE = "responseCode";


    @Test
    void 지하철_노선_생성_요청_테스트() {
        // given
        지하철_역_생성_요청("성수역");
        지하철_역_생성_요청("강남역");

        // when
        Map<String, String> body = 지하철_노선_바디_생성("green", "10", "2호선", "2", "1");
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(body);

        // then
        assertEquals(1L, response.jsonPath().getLong(ID_PATH));
        assertEquals(201, response.statusCode());
        assertEquals(CREATED_SUCCESS.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }

    @Test
    void 지하철_노선_조회_테스트() {
        // given
        지하철_2호선_생성();

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("1");

        // then
        assertEquals(200, response.statusCode());
        assertEquals("2호선", response.jsonPath().get(NAME_PATH));
        assertEquals(SUCCESS.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }

    @Test
    void 없는_지하철_노선_조회_테스트() {
        // given
        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("1");

        // then
        assertEquals(400, response.statusCode());
        assertEquals(NONE_LINE.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }

    @Test
    void 지하철_노선_목록_조회_테스트() {
        // given
        지하철_2호선_생성();

        지하철_역_생성_요청("건대역");
        지하철_역_생성_요청("어린이대공원역");
        Map<String, String> body = 지하철_노선_바디_생성("green", "10", "7호선", "4", "3");
        지하철_노선_생성_요청(body);

        // when
        ExtractableResponse<Response> response = get요청(LineStep.PATH);

        // then
        assertEquals(200, response.statusCode());
    }

    @Test
    void 지하철_노선_빈목록_조회_테스트() {
        // given
        // when
        ExtractableResponse<Response> response = get요청(LineStep.PATH);

        // then
        assertEquals(200, response.statusCode());
        assertEquals(EMPTY_INFO.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }

    @Test
    void 지하철_노선_수정_테스트(){
        // given
        지하철_2호선_생성();

        지하철_역_생성_요청("건대역");
        Map<String, String> body = 지하철_노선_바디_생성("green", "10", "신분당선", "3", "1");
        Long id = 지하철_2호선_생성_Fixture(성수역, 강남역).jsonPath().getLong(ID_PATH);
        지하철_역_생성_요청(건대역);
        UpdateLineRequest request = new UpdateLineRequest(GREEN, 신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("1", body);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(Long.toString(id), request);

        // then
        assertEquals(200, response.statusCode());
        assertEquals("신분당선", response.jsonPath().get(NAME_PATH));
        assertEquals(신분당선, response.jsonPath().get(NAME_PATH));
        assertEquals(SUCCESS.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }

    @Test
    void 없는_지하철_노선_수정_테스트(){
        Map<String, String> body = 지하철_노선_바디_생성("green", "10", "신분당선", "3", "1");
        UpdateLineRequest request = new UpdateLineRequest(GREEN, 신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("1", body);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("1", request);

        // then
        assertEquals(400, response.statusCode());
        assertEquals(404, response.statusCode());
        assertEquals(NONE_LINE.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }


    // TODO : 수정 스펙 변경됨
    @Test
    void 지하철_노선_없는_역으로_수정_테스트(){
    void 지하철_노선_수정_테스트_WHEN_중복된_이름으로_수정(){
        // given
        지하철_2호선_생성();
        Long id = 지하철_2호선_생성_Fixture(성수역, 강남역).jsonPath().getLong(ID_PATH);
        지하철_7호선_생성_Fixture(건대역, 어린이대공원역);

        UpdateLineRequest request = new UpdateLineRequest(GREEN, 칠호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(Long.toString(id), request);

        // then
        assertEquals(409, response.statusCode());
        assertEquals(DUPLICATED_LINENAME.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }

        Map<String, String> body = 지하철_노선_바디_생성("green", "10", "신분당선", "3", "1");
    @Test
    void 지하철_노선_수정_테스트_WHEN_중복된_색으로_수정(){
        // given
        Long id = 지하철_2호선_생성_Fixture(성수역, 강남역).jsonPath().getLong(ID_PATH);
        지하철_7호선_생성_Fixture(건대역, 어린이대공원역);
        UpdateLineRequest request = new UpdateLineRequest(DARKGREEN, 이호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("1", body);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(Long.toString(id), request);

        // then
        assertEquals(400, response.statusCode());
        assertEquals(409, response.statusCode());
        assertEquals(DUPLICATED_LINECOLOR.getResponseCode(), response.jsonPath().getLong(RESPONSECODE));
    }


    @Test
    void 지하철_노선_삭제_테스트() {
        // given
        지하철_2호선_생성();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("id", "1")
                .when().delete(LineStep.PATH + "/{id}")
                .then().log().all().extract();

        // then
        assertEquals(204, response.statusCode());

    }
}
