let countTotal;
let countPerPage;
let countPages;

// window[addEventListener ? 'addEventListener' : 'attachEvent'](addEventListener ? 'load' : 'onload', updateAll);

function updateAll() {
    updateCounts();
    preparePagingButtons();
    showAccounts();
}

function updateCounts() {
    countTotal = getAccountCount();
    console.log('countTotal: ' + countTotal);
    countPerPage = $('#account_count_per_page_select').val();
    if (countPerPage == null) {
        countPerPage = 3;
    }
    console.log('countPerPage: ' + countPerPage);
    countPages = Math.ceil(countTotal / countPerPage);
    console.log('countPages: ' + countPages);
}

function showAccounts(pageNumber) {
    if (pageNumber == null) {
        pageNumber = 0;
    }

    let url = '/rest/players?' +
        'pageNumber=' + pageNumber + '&' +
        'pageSize=' + countPerPage;

    console.log('Query url: ' + url);

    $('tr:has(td)').remove();

    $.get(url, function (response) {
        $.each(response, function (i, item) {
            $('<tr>').html(
                '<td>' + item.id + '</td>' +
                '<td>' + item.name + '</td>' +
                '<td>' + item.title + '</td>' +
                '<td>' + item.race + '</td>' +
                '<td>' + item.profession + '</td>' +
                '<td>' + item.level + '</td>' +
                '<td>' + new Date(item.birthday).toLocaleDateString() + '</td>' +
                '<td>' + item.banned + '</td>' +
                '<td><div class="row justify-content-md-center">' +
                '<button id="edit_button_' + item.id + '" class="btn btn-info btn-sm mx-1 col-5" ' +
                'onclick="editAccount(' + item.id + ')"><i class="fas fa-pencil-alt"></i> Edit</button>' +
                '<button id="delete_button_' + item.id + '" class="btn btn-danger btn-sm mx-1 col-5" ' +
                'onclick="deleteAccount(' + item.id + ')"><i class="fas fa-trash-alt"></i> Delete</button>' +
                '</div></td>'
            ).appendTo('#account_table');
        });
    });
}

function preparePagingButtons() {
    $('li.page-item').remove()
    for (let i = 0; i < countPages; i++) {
        let buttonTag;
        if (i === 0) {
            buttonTag = '<li class="page-item"><a class="page-link active" href="#">' + (i + 1) + '</a></li>';
        } else {
            buttonTag = '<li class="page-item"><a class="page-link" href="#">' + (i + 1) + '</a></li>';
        }
        $('#account_paging_buttons').append(buttonTag);
    }

    $('ul.pagination li a').on('click',function(e){
        e.preventDefault();
        $('a.page-link.active').removeClass('active');
        $(this).addClass('active');
        showAccounts(getCurrentPageNumber());
    });
}

function getCurrentPageNumber() {
    let buttonIndex = parseInt($('a.page-link.active').text());
    if (buttonIndex == null) {
        buttonIndex = 1;
    }
    console.log('Current page number: ' + (buttonIndex - 1));
    return buttonIndex - 1;
}

function deleteAccount(id) {
    if (id == null) {
        console.error("id is null");
        return;
    }

    if (!(confirm('Are you sure you want to delete the account (id = ' + id + ')?')))
        return;

    let url = '/rest/players/' + id;

    console.log('Delete url: ' + url);

    $.ajax({
        url: url,
        type: 'DELETE',
        async: false,
        success: function () {
            console.log("Account successfully deleted!");
            updateAll()
        }
    });
}

function editAccount(id) {
    if (id == null) {
        console.error("id is null");
        return;
    }

    let editButton = '#edit_button_' + id;
    let deleteButton = '#delete_button_' + id;

    $(deleteButton).remove();
    $(editButton)
        .removeClass('btn-info')
        .addClass('btn-success')
        .attr('onclick', 'saveAccount(' + id + ')')
        .html('<i class="fas fa-save"></i> Save');

    let row = $(editButton).parent().parent().parent();
    let cells = row.children();

    makeTextCellEditable('name', cells[1], id);
    makeTextCellEditable('title', cells[2], id);
    makeRaceSelectCellEditable(cells[3], id);
    makeProfessionSelectCellEditable(cells[4], id);
    makeBannedSelectCellEditable(cells[7], id);
}

function saveAccount(id) {
    let name = $('#name_input_' + id).val();
    let title = $('#title_input_' + id).val();
    let race = $('#race_select_' + id).val();
    let profession = $('#profession_select_' + id).val();
    let banned = $('#banned_select_' + id).val();

    let url = '/rest/players/' + id;

    let json = JSON.stringify({
        'name': name,
        'title': title,
        'race': race,
        'profession': profession,
        'banned': banned
    });

    console.log('Save url: ' + url);
    console.log('Json: ' + json);

    $.ajax({
        url: url,
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json;charset=UTF-8',
        async: false,
        data: json,
        success: function () {
            console.log("Account successfully saved!");
            showAccounts(getCurrentPageNumber());
        }
    });
}

function createAccount() {
    let name = $('#name_input').val();
    let title = $('#title_input').val();
    let race = $('#race_select').val();
    let profession = $('#profession_select').val();
    let level = $('#level_input').val();
    let birthday = $('#birthday_input').val();
    let banned = $('#banned_select').val();

    let url = '/rest/players';

    let json = JSON.stringify({
        'name': name,
        'title': title,
        'race': race,
        'profession': profession,
        'level': level,
        'birthday': new Date(birthday).getTime(),
        'banned': banned
    });

    console.log('Create url: ' + url);
    console.log('Json: ' + json);

    $.ajax({
        url: url,
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json;charset=UTF-8',
        async: false,
        data: json,
        success: function () {
            console.log("Account successfully created!");
        },
        error: function(e) {
            console.error('Create account failed!');
        }
    });
}

function makeTextCellEditable(idPrefix, cell, id) {
    cell.innerHTML = '<input id="' + idPrefix + '_input_'+ id +'" type="text" ' +
        'value=' + cell.innerHTML +' class="form-control form-control-sm">';
    return cell;
}

function makeRaceSelectCellEditable(cell, id) {
    let elementId = 'race_select_'+ id;
    let currentValue = cell.innerHTML;
    cell.innerHTML = '<label for="race"/>'
        + '<select id="'+ elementId +'" name="race">'
        + '<option value="HUMAN">HUMAN</option>'
        + '<option value="DWARF">DWARF</option>'
        + '<option value="ELF">ELF</option>'
        + '<option value="GIANT">GIANT</option>'
        + '<option value="ORC">ORC</option>'
        + '<option value="TROLL">TROLL</option>'
        + '<option value="HOBBIT">HOBBIT</option>'
        + '</select>';
    $('#' + elementId).val(currentValue).change();
    return cell;
}

function makeProfessionSelectCellEditable(cell, id) {
    let elementId = 'profession_select_'+ id;
    let currentValue = cell.innerHTML;
    cell.innerHTML = '<label for="profession"/>'
        + '<select id="'+ elementId +'" name="profession">'
        + '<option value="WARRIOR">WARRIOR</option>'
        + '<option value="ROGUE">ROGUE</option>'
        + '<option value="SORCERER">SORCERER</option>'
        + '<option value="CLERIC">CLERIC</option>'
        + '<option value="PALADIN">PALADIN</option>'
        + '<option value="NAZGUL">NAZGUL</option>'
        + '<option value="WARLOCK">WARLOCK</option>'
        + '<option value="DRUID">DRUID</option>'
        + '</select>';
    $('#' + elementId).val(currentValue).change();
    return cell;
}

function makeBannedSelectCellEditable(cell, id) {
    let elementId = 'banned_select_'+ id;
    let currentValue = cell.innerHTML;
    cell.innerHTML = '<label for="banned"/>'
        + '<select id="'+ elementId +'" name="banned">'
        + '<option value="true">true</option>'
        + '<option value="false">false</option>'
        + '</select>';
    $('#' + elementId).val(currentValue).change();
    return cell;
}

function getAccountCount() {
    let url = '/rest/players/count';
    let count = 0;

    $.ajax({
        url: url,
        async: false,
        success: function (result) {
            count = parseInt(result);
        }
    });

    return count;
}
