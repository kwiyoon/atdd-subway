package kuit.subway.service;

import kuit.subway.domain.Line;
import kuit.subway.domain.Station;
import kuit.subway.repository.LineRepository;
import kuit.subway.request.line.LineRequest;
import kuit.subway.response.line.CreateLineResponse;
import kuit.subway.response.line.ShowLineResponse;
import kuit.subway.utils.exception.LineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kuit.subway.utils.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    @Transactional
    public CreateLineResponse createLine(LineRequest request) {
        Station downStation = stationService.findById(request.getDownStationId());
        Station upStation = stationService.findById(request.getUpStationId());

        checkDuplicateName(request.getName());

        Line line = Line.builder()
                .downStation(downStation)
                .upStation(upStation)
                .distance(request.getDistance())
                .name(request.getName())
                .color(request.getColor())
                .build();

        lineRepository.save(line);

        return CreateLineResponse.from(line);
    }

    public ShowLineResponse getLine(Long id) {
        Line line = findById(id);
        return ShowLineResponse.from(line);
    }

    public List<ShowLineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();

        if(lines.isEmpty()) throw new LineException(EMPTY_INFO);

        return lines.stream().map(ShowLineResponse::from).toList();
    }

    @Transactional
    public ShowLineResponse updateLine(Long id, LineRequest request) {
        Line line = findById(id);
        Station downStation = stationService.findById(request.getDownStationId());
        Station upStation = stationService.findById(request.getUpStationId());

        checkDuplicateName(request.getName());

        line.update(request, upStation, downStation);

        return ShowLineResponse.from(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineException(NONE_LINE));
    }

    private void checkDuplicateName(String name) {
        if(lineRepository.existsLineByName(name))
            throw new LineException(DUPLICATED_LINE);
    }


}
